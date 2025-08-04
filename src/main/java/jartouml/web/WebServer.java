package jartouml.web;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.UploadedFile;
import jartouml.core.*;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WebServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create(cfg -> {
            cfg.staticFiles.add(s -> { s.directory="/public"; s.location=Location.CLASSPATH; });
            cfg.http.maxRequestSize = 50L * 1024 * 1024; // 50 MB
        }).exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500).result("Upload failed: " + e);
        }).start(7070);


        app.get("/", ctx -> ctx.redirect("/index.html"));

        app.post("/upload", ctx -> {
            try {
                UploadedFile file = ctx.uploadedFile("jar");
                if (file == null) { ctx.status(400).result("Файл не получен"); return; }

                Path tmpJar = Files.createTempFile("upload-", ".jar");
                try (InputStream in = file.content()) {
                    Files.copy(in, tmpJar, StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("Processing JAR: " + tmpJar); // лог
                String uml;
                try {
                    uml = runExtraction(tmpJar.toString());
                } finally {
                    try { Files.deleteIfExists(tmpJar); } catch (IOException ignore) {}
                }
                String svg = renderPlantUmlToSvg(uml);

                Path out = Files.createTempFile("uml-", ".txt");
                Files.writeString(out, uml);

                String template = """
                <html><head><meta charset="utf-8"><title>Результат</title></head>
                <body style="font-family: sans-serif; max-width: 900px; margin: 2rem auto;">
                  <h2> Class Diagram </h2>
                  <div>{svg}</div>
                  <h2> UML text </h2>
                  <textarea style="width:100%; height:60vh;">{uml}</textarea>
                  <p><a href="/download?path={path}">Скачать файл</a></p>
                  <p><a href="/">← Back</a></p>
                </body></html>
                """;
                String html = template
                        .replace("{svg}", svg)                                   // SVG inline (уже готовый XML)
                        .replace("{uml}", escapeHtml(uml))                        // экранируем HTML-опасные символы
                        .replace("{path}", out.toString().replace("\\", "\\\\")); // экранируем backslash для URL

                ctx.html(html);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Upload failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        });

        // Отдача сохранённого файла
        app.get("/download", ctx -> {
            String p = ctx.queryParam("path");
            if (p == null) { ctx.status(400).result("Нет пути"); return; }
            Path path = Path.of(p);
            if (!Files.exists(path)) { ctx.status(404).result("Файл не найден"); return; }
            ctx.result(Files.newInputStream(path))
                    .header("Content-Disposition", "attachment; filename=\"output.txt\"");
        });
    }

    // Вынесенная логика: почти то же самое, что в Main.main
    private static String runExtraction(String jarFileName) throws IOException {
        List<Class<?>> classList = ClassExtractor.extract(jarFileName);
        InformationGatherer gatherer = new InformationGatherer(classList);
        gatherer.gatherInformation();

        List<ClassInformation> classInfos = gatherer.getClassListInformation();
        List<RelationshipInformation> relationships = gatherer.getRelationshipListInformation();

        Set<String> ignoredClass = new HashSet<>();
        ignoredClass.add("java.util");
        ignoredClass.add("java.lang");

        UmlExportConfiguration config =
                new UmlExportConfiguration(UmlFormat.PLANTUML, false, true, true, true, ignoredClass);

        UmlExporter exporter = UmlExporterFactory.getExporter(config.getFormat());
        return exporter.export(classInfos, relationships, config);
    }

    private static String renderPlantUmlToSvg(String plantUml) throws IOException {
        SourceStringReader reader = new SourceStringReader(plantUml);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
            return os.toString(StandardCharsets.UTF_8);
        }
    }

    private static String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
