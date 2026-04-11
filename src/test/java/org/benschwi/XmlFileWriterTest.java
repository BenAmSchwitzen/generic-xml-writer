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

    private static record Dummy(String name, Integer age, boolean isHealthy) {}
    private static record RecursiveFieldDummy(String name, List<String> listValues) {}

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
            List<Dummy> dummyList = List.of();

            assertThatExceptionOfType(XMLFileWriterException.class).isThrownBy(() -> testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "DummyCollection", dummyList));
        }

        @Nested
        class CreateXmlFileTests_recursion {

            @Test
            void testCreateAndWriteXmlFile_oneFieldHasCollectionType() {
                XmlFileWriter<RecursiveFieldDummy> testInstance = new XmlFileWriter<>(
                        new XmlField<>("iAmAListContainer", RecursiveFieldDummy::name),
                        new XmlField<>("collection", RecursiveFieldDummy::listValues)
                );
                List<RecursiveFieldDummy> recursiveFieldDummyList = List.of(
                        new RecursiveFieldDummy("Dummy1", List.of("item1", "item2")),
                        new RecursiveFieldDummy("Dummy2", List.of("item1", "item2", "item3", "item4")),
                        new RecursiveFieldDummy("Dummy2", List.of("item1", "item2", "item3", "item4", "item5", "item6"))
                );
                testInstance.writeAndCreateXMLFile(String.valueOf(DIR_PATH), getRandomFileName(), "RecursiveFieldDummyRoot", recursiveFieldDummyList);
            }

        }

    }

    private static String getRandomFileName() {
        return UUID.randomUUID().toString();
    }

}
