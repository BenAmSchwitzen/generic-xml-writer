package org.benschwi;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;


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
            XmlField<Dummy> xmlField1 = new XmlField<>("name", Dummy::name);
            XmlField<Dummy> xmlField2 = new XmlField<>("age", Dummy::age);

            assertThatNoException().isThrownBy(() -> new XmlFileWriter<>(xmlField1, xmlField2));
        }

        @Test
        void testCreateXmlFileWriter_xmlFieldsIsNull() {
           assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> new XmlFileWriter<Dummy>(null));
        }

        @Test
        void testCreateXmlFileWriter_xmlFieldsIsEmpty() {
            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(XmlFileWriter<Dummy>::new);
        }

    }

    @Nested
    class CreateXmlFileTests {

        @Test
        void testCreateAndWriteXmlFile() {
            String expectedFileName = getRandomFileName();
            String expectedRootElementName = "DummyCollection";
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("age", Dummy::age),
                    new XmlField<>("healthy", Dummy::isHealthy)
            );
            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatNoException().isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), expectedFileName, expectedRootElementName, dummyList));
            assertThat(Files.exists(DIR_PATH.resolve(expectedFileName + ".xml"))).isTrue();
            //eventuell werte rauslesen oder zeilen zählen die ich erweatre weiss ich ja vorher wenn ich values genau kenne wie hier
            // und mock tests fehlen auch noch
        }

        @Test
        @Disabled("Null values muss ich mich noch drum kümmern. Sowohl bei den Collection instances selbst, als auch bei deren attribute values, muss noch überllegt werden")
        void testCreateAndWriteXmlFile_OneOfTheListElementsIsNull() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );
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
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );
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
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );
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
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );

            List<Dummy> dummyList = List.of(
                    new Dummy("Dummy1", 18, true),
                    new Dummy("Dummy2", 27, false),
                    new Dummy("Dummy4", 133, true));

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), rootElementName, dummyList));
        }

        @Test
        void testCreateAndWriteXmlFile_elementCollectionIsNull() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );

            List<Dummy> dummyList = null;

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "DummyCollection", dummyList));
        }

        @Test
        void testCreateAndWriteXmlFile_elementCollectionIsEmpty() {
            XmlFileWriter<Dummy> testInstance = new XmlFileWriter<>(
                    new XmlField<>("name", Dummy::name),
                    new XmlField<>("name", Dummy::age),
                    new XmlField<>("name", Dummy::isHealthy)
            );

            //<Dummy> dummyList = Collections.emptyList();
            //List<Dummy> dummyList = List.of();
            // which one is better
            List<Dummy> dummyList = List.of();

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "DummyCollection", dummyList));
        }

    }
    // hierfür auch record gut?
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
