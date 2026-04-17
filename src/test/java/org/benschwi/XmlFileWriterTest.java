package org.benschwi;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

public class XmlFileWriterTest {

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
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);
            String expectedXMLDeclaration = "";
            String expectedRootElementName = "DummyCollection";
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatNoException().isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), expectedRootElementName, dummyList));

            // check noch ob file erstellt wurde am richtigen Ort mit richtigen Namen. eventuell werte rauslesen oder zeilen zähne die ich erweatre weiss ich ja vorher wenn ich values genau kenne wie hier
        }

        @Test
        @Disabled("Null values muss ich mich noch drum kümmern. Sowohl bei den Collection instances selbst, als auch bei deren attribute values, muss noch überllegt werden")
        void testCreateAndWriteXmlFile_OneOfTheListElementsIsNull() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);
            String expectedXMLDeclaration = "";
            String expectedRootElementName = "DummyCollection";
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    null,
                    new Dummy("Dummy4", 133, true));

            assertThatNoException().isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), expectedRootElementName, dummyList));

        }

        @ParameterizedTest()
        @NullSource
        @ValueSource(strings = {" ", "    ", "     "})
        void testCreateAndWriteXmlFile_destinationPathIsInvalid(String destinationPath) {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(destinationPath, getRandomFileName(), "DummyCollection", dummyList));
        }

        @ParameterizedTest()
        @NullSource
        @ValueSource(strings = {" ", "    ", "     "})
        void testCreateAndWriteXmlFile_fileNameIsInvalid(String fileName) {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);

            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), fileName, "DummyCollection", dummyList));
        }

        @ParameterizedTest()
        @NullSource
        @ValueSource(strings = {" ", "    ", "     "})
        void testCreateAndWriteXmlFile_rootElementNameIsInvalid(String rootElementName) {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);

            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), rootElementName, dummyList));
        }

        @Test
        void testCreateAndWriteXmlFile_elementCollectionIsNull() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);

            List<Dummy> dummyList = null;

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "DummyCollection", dummyList));
        }

        @Test
        void testCreateAndWriteXmlFile_elementCollectionIsEmpty() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(new String[]{"name", "age", "healthy"},
                    Dummy::name, Dummy::age, Dummy::isHealthy);

            //<Dummy> dummyList = Collections.emptyList();
            //List<Dummy> dummyList = List.of();
            // which one is better
            List<Dummy> dummyList = List.of();

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "DummyCollection", dummyList));
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
    // und null values bei collections testen udn bei den isntanzen der collections selbst die Attributte, wei damit umgehen?

    // schaue web fragen :)
    // ist record hier missbraucht als Dummy oder genau richtig?

}
