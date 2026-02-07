package com.example.project2;

import java.io.*;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class HelloApplication extends Application {
    int[] arrayStore = new int[256]; // 2^8 =256 bit
    int maxLength = 0;
    Heap heap = new Heap(arrayStore);
    TableNode[] table = new TableNode[256];
    Header head;
    HeapNode root;
    static TextField browseField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        buttonsFrame(stage);
    }

    public void buttonsFrame(Stage stage) {
        BorderPane pane = new BorderPane();

        Button browse = designButton("Choose File", 150, 50, "#014920", "#FDFDFD");
        Button compression = designButton("Compression", 150, 50, "#014920", "#FDFDFD");
        Button decompression = designButton("Decompression", 150, 50, "#014920", "#FDFDFD");
        Button status = designButton("Table", 150, 50, "#014920", "#FDFDFD");
        Button header = designButton("Header", 150, 50, "#014920", "#FDFDFD");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(browse, compression, decompression, status, header);

        pane.setCenter(vbox);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane, 0, 0);
        stage.setScene(scene);
        stage.setTitle("Choose");
        stage.setMaximized(true);
        stage.show();

        decompression.setOnAction(e1 -> {
            try {
                decompressFile(stage);
                displaySuccessAlert("Decompressed File Is Ready");
            } catch (Exception e2) {
                displayErrorAlert("Something Is Wrong");
            }
        });

        browse.setOnAction(e1 -> {
            browseFile(stage);

            byte[] buffer = new byte[256];
            try {
                File filePath = new File(browseField.getText());
                int x = filePath.getName().lastIndexOf(".");
                String huf = filePath.getName().substring(x + 1);
                if (huf.equalsIgnoreCase("huf"))
                    throw new Exception();
                InputStream inputstream = new FileInputStream(filePath);
                for (int i = 0; i < arrayStore.length; i++)
                    arrayStore[i] = 0;
                while (inputstream.available() > 0) {
                    int numBytes = inputstream.read(buffer);
                    for (int i = 0; i < numBytes; i++) {
                        int ch = buffer[i];
                        if (ch < 0) {
                            int r = 256 - (-2 * ch);
                            ch = r + (-1 * ch);
                        }
                        arrayStore[ch]++;
                    }
                }

                huffmanCode(arrayStore);

                compression.setOnAction(e2 -> {
                    try {
                        writeHuffmanToFile(table, head);
                        head.content = "";
                        displaySuccessAlert("Compressed File Is Ready");
                    } catch (Exception e3) {
                        displayErrorAlert("Something Is Wrong");
                    }
                });

                status.setOnAction(e4 -> {
                    BorderPane pane1 = new BorderPane();

                    Button back1 = designButton("Back", 150, 50, "#014920", "#FDFDFD");

                    TableView<TableNode> tableview = new TableView<>();
                    tableview.setPrefSize(200, 500);

                    // ascii code as string
                    TableColumn<TableNode, String> asciiCol = new TableColumn<>("ASCII");
                    asciiCol.setMinWidth(100);
                    asciiCol.setCellValueFactory(cellData -> createStringPropertyFromInteger(cellData.getValue().getCharacter()));

                    // ascii code as number
                    TableColumn<TableNode, Integer> characterCol = new TableColumn<>("Character");
                    characterCol.setMinWidth(100);
                    characterCol.setCellValueFactory(new PropertyValueFactory<>("character"));

                    TableColumn<TableNode, Integer> frequencyCol = new TableColumn<>("Frequency");
                    frequencyCol.setMinWidth(100);
                    frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

                    TableColumn<TableNode, Integer> bitLengthCol = new TableColumn<>("Bit Length");
                    bitLengthCol.setMinWidth(100);
                    bitLengthCol.setCellValueFactory(new PropertyValueFactory<>("bitLength"));

                    TableColumn<TableNode, String> huffmanCodeCol = new TableColumn<>("Huffman Code");
                    huffmanCodeCol.setMinWidth(100);
                    huffmanCodeCol.setCellValueFactory(new PropertyValueFactory<>("huffmanCode"));

                    ObservableList<TableNode> dataTable = FXCollections.observableArrayList();
                    for (int i = 0; i < table.length; i++)
                        dataTable.add(table[i]);

                    tableview.setItems(dataTable);
                    tableview.getColumns().addAll(asciiCol, characterCol, frequencyCol, bitLengthCol, huffmanCodeCol);

                    VBox vbox1 = new VBox();
                    vbox1.setSpacing(10);
                    vbox1.setAlignment(Pos.CENTER);
                    vbox1.getChildren().addAll(tableview, back1);

                    pane1.setCenter(vbox1);
                    pane1.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

                    Scene scene1 = new Scene(pane1, 700, 600);
                    stage.setScene(scene1);
                    stage.setTitle("Table");
                    // stage.setMaximized(true);
                    stage.show();

                    back1.setOnAction(e5 -> {
                        stage.setScene(scene);
                    });
                });

                header.setOnAction(e6 -> {
                    head = new Header(root);
                    String headerString = head.headerTree(root);

                    BorderPane pane2 = new BorderPane();

                    Button back2 = designButton("Back", 150, 50, "#014920", "#FDFDFD");

                    TextArea text = new TextArea();
                    text.setMaxSize(500, 300);

                    RadioButton fileExtension = new RadioButton("The Extension Of File");
                    RadioButton headerSize = new RadioButton("The Size Of Header");
                    RadioButton headerTree = new RadioButton("The Tree Header");

                    ToggleGroup toggle = new ToggleGroup();
                    toggle.getToggles().addAll(fileExtension, headerSize, headerTree);

                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER);
                    hbox.getChildren().addAll(fileExtension, headerSize, headerTree);

                    VBox vbox2 = new VBox();
                    vbox2.setSpacing(10);
                    vbox2.setAlignment(Pos.CENTER);
                    vbox2.getChildren().addAll(hbox, text, back2);

                    pane2.setCenter(vbox2);
                    pane2.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

                    Scene scene2 = new Scene(pane2, 700, 600);
                    stage.setScene(scene2);
                    stage.setTitle("Header");
                    // stage.setMaximized(true);
                    stage.show();

                    back2.setOnAction(e7 -> {
                        stage.setScene(scene);
                    });

                    int y = filePath.getName().lastIndexOf(".");
                    String string = filePath.getName().substring(y + 1);
                    fileExtension.selectedProperty().addListener(e8 -> {
                        text.setText("The File Extension :" + string);
                    });

                    headerSize.selectedProperty().addListener(e9 -> {
                        text.setText("The Size :" + (((head.numberRepresentation.length() / 8) + 1) + head.characterRepresentation.length()));
                    });

                    headerTree.selectedProperty().addListener(e10 -> {
                        text.setText(headerString);
                    });
                });

                inputstream.close();

            } catch (Exception e11) {
                displayErrorAlert("Something Is Wrong");
            }
        });

    }

    // the functions design

    public Button designButton(String name, int width, int height, String color1, String color2) {
        Button button = new Button(name);
        button.setPrefSize(width, height);
        button.setStyle("-fx-background-color:" + color1 + "; -fx-background-radius: 15px; -fx-text-fill:" + color2);
        return button;
    }

    public void displaySuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // the functions

    public SimpleStringProperty createStringPropertyFromInteger(int intValue) {
        return new SimpleStringProperty(String.valueOf((char) intValue));
    }

    public void browseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            browseField.setText(file.getAbsolutePath());
        }
    }

    public void huffmanCode(int[] arr) {
        Heap heap = new Heap(arr);
        // build heap to the nodes of bytes
        heap.buildingHeap(arr);
        root = new HeapNode();
        for (int i = 1; i < heap.nonZeroCount; i++) {
            HeapNode z = new HeapNode();
            // delete the two minimum nodes of the heap
            HeapNode x = heap.deleteMin(arr);
            HeapNode y = heap.deleteMin(arr);
            // insert new node and put the first min in the left and the second in the right
            z.leftChild = x;
            z.rightChild = y;
            // the frequency of the inserted node is the addition of the two minimum nodes deleted
            z.frequency = x.frequency + y.frequency;
            root = z;
            heap.insert(z);
        }
        printCode(heap, root, "");
        informationTable(heap, arr);
        head = new Header(root);
    }

    public void informationTable(Heap heap, int[] arr) {
        for (int i = 0; i < table.length; i++)
            table[i] = new TableNode();
        for (int i = 0; i < table.length; i++) {
            table[i].character = i;
            table[i].frequency = arr[i];
            table[heap.heapArray[i].characterValue].huffmanCode = heap.heapArray[heap.heapArray[i].characterValue].huffmanCode;
            table[i].bitLength = table[i].huffmanCode.length();
        }
    }

    public void printCode(Heap heap, HeapNode root, String str) {
        if (root.leftChild == null && root.rightChild == null) {
            heap.heapArray[root.characterValue].huffmanCode = str;
            root.huffmanCode = root.huffmanCode + str;
            // get the total number of bits in the chosen file
            maxLength += root.frequency * (root.huffmanCode.length());
            return;
        }
        printCode(heap, root.leftChild, str + root.leftRow);
        printCode(heap, root.rightChild, str + root.rightRow);
    }

    public void writeHuffmanToFile(TableNode[] table, Header header) throws Exception {
        File path = new File(browseField.getText());
        InputStream file = new FileInputStream(path);
        byte[] buffer = new byte[200];
        int y = path.getName().lastIndexOf(".");
        String string = path.getName().substring(0, y);
        DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(string + ".huf"));
        byte temp = 0;
        // build the header tree
        header.headerTree(header.root);
        int x = path.getName().lastIndexOf(".");
        String field = path.getName().substring(x + 1);
        // write the length of the extension file to the compressed file
        fileOut.write(field.length());
        // create array of bytes and write the ext of the file to the out file
        byte[] extensionBuffer = new byte[field.length()];
        for (int i = 0; i < extensionBuffer.length; i++) {
            extensionBuffer[i] = (byte) field.charAt(i);
        }
        fileOut.write(extensionBuffer);
        // write the size of the header that has the leaf and non leaf nodes and bytes
        fileOut.write(header.numberRepresentation.length() / 8 + 1);
        fileOut.writeInt(header.numberRepresentation.length());
        // write the header tree to the out file
        String headerString = header.numberRepresentation;
        int headerBytes = 8;
        int z = 0;
        int headerLen = header.numberRepresentation.length();
        for (int i = headerLen; i >= headerBytes; i -= 8) {
            temp = toByte(headerString.substring(z, z + 8));
            fileOut.write(temp);
            headerLen -= 8;
            z += 8;
        }
        // if the header length greater than 8
        if (headerLen > 0) {
            headerString = headerString.substring(z);
            while (headerString.length() < 8)
                headerString += "0";
            temp = toByte(headerString.substring(0, 8));
            fileOut.write(temp);
        }
        byte[] charBuffer = new byte[header.characterRepresentation.length()];
        for (int i = 0; i < charBuffer.length; i++)
            charBuffer[i] = (byte) header.characterRepresentation.charAt(i);
        fileOut.write(header.characterRepresentation.length() - 1);
        fileOut.write(charBuffer);
        // write the number of zeroes that i added when compress to complete to 8 bits
        fileOut.write(8 - (maxLength % 8));
        int sum = 0;
        String huffman = "";
        while (file.available() > 0) {
            // read buffer from file
            int numBytes = file.read(buffer);
            for (int i = 0; i < numBytes; i++) {
                int c = buffer[i];
                if (c < 0) {
                    int r = 256 - (-2 * c);
                    c = r + (-1 * c);
                }
                // convert each 32 bytes (256 bits)
                int available = 256 - sum;
                // if the length of the huffman code for this byte less than the available space
                // add this huffman code to the available space and add its length to sum to decrement the available space
                if (available >= table[c].bitLength) {
                    sum += table[c].bitLength;
                    huffman += table[c].huffmanCode;
                    // if the sum reach to 256 that means that the number of bytes is 32 and there is no available space
                    if (sum == 256) {
                        // create array of 32 bytes and write it to the out file
                        byte[] buff = compress32Bytes(huffman);
                        fileOut.write(buff);
                        // make the available space is zero and huffman code empty
                        sum = 0;
                        huffman = "";
                    }
                    // if the available space less than huffman code length
                } else {
                    // substring the huffman code string from 0 to the available space and append it to the huffman string
                    huffman += table[c].huffmanCode.substring(0, available);
                    // create array of 32 bytes and write it to the out file
                    byte[] b = compress32Bytes(huffman);
                    fileOut.write(b);
                    // make the available space equal to the remain string and the sum equal to its length
                    huffman = table[c].huffmanCode.substring(available);
                    sum = huffman.length();
                }
            }
        }
        // if the reading of file end and the there is bytes in the huffman string
        int k = 0;
        String remainderHuff = huffman;
        while (sum >= 8) {
            // convert every 8 bits to byte and write it to the file then substring the remHuf string
            temp = toByte(huffman.substring(k, k + 8));
            if (remainderHuff.length() > 8) {
                remainderHuff = remainderHuff.substring(8);
            }
            fileOut.write(temp);
            sum -= 8;
            k += 8;
        }
        // if there is bits less than 8 append zeroes to it then convert to byte and write it to the file
        if (sum < 8) {
            while (remainderHuff.length() < 8)
                remainderHuff += "0";
            temp = toByte(remainderHuff.substring(0, 8));
            fileOut.write(temp);
        }
        file.close();
        fileOut.flush();
        fileOut.close();
    }

    public byte[] compress32Bytes(String str) {
        byte[] bytes = new byte[32];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = 0;
        while (str.length() < 256)
            str += "0";
        byte answer;
        int count = 0;
        for (int i = 0; i < 256; i += 8) {
            answer = toByte(str.substring(i, i + 8));
            bytes[count++] = answer;
        }
        return bytes;
    }

    public byte toByte(String str) {
        int bit = 0;
        byte temp = 0;
        byte firstByte = 0;
        for (int i = 0; i < str.length(); bit++, i++)
            if (str.charAt(i) == '1') {
                firstByte = (byte) (1 << (7 - bit % 8));
                temp |= firstByte;
            }
        return temp;
    }

    public void decompressFile(Stage stage) throws Exception {
        // browse compressed file and read it
        TextField path = new TextField();
        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new ExtensionFilter("huf Files", "*.huf"));
        File chooseFile = filechooser.showOpenDialog(stage);
        if (chooseFile != null)
            path.setText(chooseFile.getAbsolutePath());
        File fileIn = new File(path.getText());
        DataInputStream input;
        Header header = new Header();
        input = new DataInputStream(new FileInputStream(fileIn));
        int x = fileIn.getName().lastIndexOf(".");
        String firstName = fileIn.getName().substring(0, x);
        // set the browsed file in the header and read header from the browsed file
        header.setInputStream(input);
        header.readHeader();
        // find the number of bits of the original file before compressing
        int bytesSize = (input.available() * 8);
        int numOfAllBits = bytesSize - header.numOfAdditionalZeros;
        OutputStream fileOut = new FileOutputStream(firstName + "." + header.fileExtension);
        // create array of bytes
        byte[] buffer = new byte[32];
        byte[] outBuffer = new byte[32];
        // build the tree that is read from the compressed file
        header.buildHeaderTree(header.binaryString, header.charactersString, header.headerTree);
        HeapNode tree = header.headerTree;
        // countBytes to count the number of bytes that will be writen to the out file
        int countBytes = 0;
        // counts to count the bytes that read from the in file
        int counts = 0;
        // counter to count the number of bits read
        int counter = 0;
        // read buffer from in file
        int size = input.read(buffer);
        // convert the first byte to binary string and increment counts
        int c1 = buffer[counts];
        if (c1 < 0)
            c1 += 256;
        String binary = Integer.toBinaryString(c1);
        while (binary.length() < 8)
            binary = "0" + binary;
        counts++;
        while (counter < numOfAllBits) {
            // i to count the bits in the binary string
            int i = 0;
            while (tree.leftChild != null && tree.rightChild != null) {
                counter++;
                // i reach to the end of the string append the next byte and convert it to binary string
                if (i == (binary.length())) {
                    // if counts reach to the last index of the array read the next buffer and set counts zero
                    if (counts == (size)) {
                        size = input.read(buffer);
                        counts = 0;
                    }
                    int c2 = buffer[counts];
                    if (c2 < 0)
                        c2 += 256;
                    String s = Integer.toBinaryString(c2);
                    while (s.length() < 8)
                        s = "0" + s;
                    binary += s;
                    counts++;
                }
                // if the bit is zero go to left
                if (binary.charAt(i) == '0')
                    tree = tree.leftChild;
                    // if the bit is 1 go to write
                else
                    tree = tree.rightChild;
                i++;
            }
            // substring the found character from the string
            binary = binary.substring(i);
            //add the character to the out buffer
            outBuffer[countBytes] = (byte) tree.characterValue;
            countBytes++;
            // when the number of bytes reach to 32 in the array write the buffer in to out file and set countBytes zero
            if (countBytes == 32) {
                fileOut.write(outBuffer);
                countBytes = 0;
            }
            // return to the root when find the character
            tree = header.headerTree;
        }
        // if the number of bytes less than 32 bytes
        if (countBytes > 0) {
            fileOut.write(outBuffer, 0, countBytes);
        }
        input.close();
        fileOut.flush();
        fileOut.close();
    }
}
