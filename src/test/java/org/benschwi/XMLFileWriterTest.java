package org.benschwi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

public class XMLFileWriterTest {

    private static final Path DIR_PATH = Path.of("target").resolve("testFiles");

    // wie cleare ich aber beim nächsten Test automatisch die files
    @BeforeAll
    static void setUp() {
        if(Files.notExists(DIR_PATH)) {
            try {
                Files.createDirectory(DIR_PATH);
            } catch (IOException e) {
                fail("could not create the directory that stores all the files being created during the test process");
            }
        }
    }

    @Nested
    class CreateXmlFileWriterTests {

        @Test
        void testCreateXmlFileWriter() {
            Function<Dummy, Object> func1 = Dummy::name;
            Function<Dummy, Object> func2 = Dummy::age; // hier auslagern nach oben defineiren in class weil will schauen ob deshalb so lange. wäre es mit reflection langsamer
            String[] tagNames = {"name", "age"};

            assertThatNoException().isThrownBy(() -> new XmlFileWriter<Dummy>(tagNames, func1, func2));
        }

        @Test
        void testCreateXmlFileWriter_xmlFieldNamesIsNull() {
            Function<Dummy, Object> func1 = Dummy::name;
            Function<Dummy, Object> func2 = Dummy::age;

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> new XmlFileWriter<Dummy>(null, func1, func2));
        }

        @Test
        void testCreateXmlFileWriter_xmlFieldsIsNull() {
            String[] tagNames = {"name", "age"};
            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> new XmlFileWriter<Dummy>(tagNames));
        }

        @Test
        void testCreateXmlFileWriter_xmlFieldNamesAndXmlFieldsSizeMismatch() {
            Function<Dummy, Object> func1 = Dummy::name;
            String[] tagNames = {"name", "age"};
            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> new XmlFileWriter<Dummy>(tagNames));
        }

    }

    @Nested
    class CreateXmlFileTests {

        @Test
        void testCreateAndWriteXmlFile() {
            Function<Dummy, Object> func1 = Dummy::name;
            Function<Dummy, Object> func2 = Dummy::age;
            Function<Dummy, Object> func3 = Dummy::isHealthy;
            String[] tagNames = {"name", "age", "healthy"};
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(tagNames, func1, func2, func3);

            String expectedXMLDeclaration = "";
            String expectedRootElementName = "DummyCollection";
            String expectedElementName = "Dummy";
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    null,
                    new Dummy("Dummy4", 133, true));

            assertThatNoException().isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), expectedRootElementName, dummyList));

            // check noch ob file erstellt wurde am richtigen Ort mit richtigen Namen. eventuell werte rauslesen oder zeilen zähne die ich erweatre weiss ich ja vorher wenn ich values genau kenne wie hier
        }

        @Test
        void testCreateAndWriteXmlFile_OneOfTheListElementsIsNull() {
            Function<Dummy, Object> func1 = Dummy::name;
            Function<Dummy, Object> func2 = Dummy::age;
            Function<Dummy, Object> func3 = Dummy::isHealthy;
            String[] tagNames = {"name", "age", "healthy"};
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(tagNames, func1, func2, func3);

            String expectedXMLDeclaration = "";
            String expectedRootElementName = "DummyCollection";
            String expectedElementName = "Dummy";
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatNoException().isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), expectedRootElementName, dummyList));

        }

    }

    private static record Dummy(String name, Integer age, boolean isHealthy) {}

    private static class Dummy1 {
        private final String name;
        private final Integer age;
        private final Boolean isHealthy;

        public Dummy1(String name, Integer age, boolean isHealthy) {
            this.name = name;
            this.age = age;
            this.isHealthy = isHealthy;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public Boolean getHealthy() {
            return isHealthy;
        }
    }

    private static String getRandomFileName() {
        return UUID.randomUUID().toString();
    } // Dachte erst gut aber verfälscht das nicht Ergebnis der Tests, also in Bezug auf Schnelligkeit

}
