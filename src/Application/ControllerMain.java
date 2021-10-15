package Application;

import Dictionary.Dictionary;
import Dictionary.DictionaryManagement;
import Dictionary.Word;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ControllerMain implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private HBox speek;
    @FXML
    private HBox denification;
    @FXML
    private HBox titleApp;

    @FXML
    private TextField inputWord;

    @FXML
    private Label wordTarget;

    @FXML
    private Label wordExplain;

    @FXML
    private ListView<String> wordList = new ListView<String>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Text icon = GlyphsDude.createIcon(FontAwesomeIcons.NAVICON, "24px");
        icon.setId("nav");
        Label denificationTitle = new Label("Denification");
        denificationTitle.setId("denificationTitle");
        denification.getChildren().add(icon);
        denification.getChildren().add(denificationTitle);

        Text logo = GlyphsDude.createIcon(FontAwesomeIcons.BOOKMARK, "20px");
        logo.setId("logo");
        Label titleHead = new Label("Advance English Dictionary");
        titleHead.setId("titleHead");
        titleApp.getChildren().add(logo);
        titleApp.getChildren().add(titleHead);

        speek.getChildren().add(GlyphsDude.createIcon(FontAwesomeIcons.VOLUME_UP, "24px"));

        // bat su kien khi nhap tu tim kiem
        inputWord.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            try {
                wordList.getItems().clear();

                // lay ra tu vung tu Dictionary.DictionaryManagement
                Dictionary dictionary = DictionaryManagement.getDictionary();

                // hien thi: goi y tu se tim
                for (int i = 0; i < dictionary.getWords().size(); i++) {
                    Word element = dictionary.getWords().get(i);

                    // kiem tra newValue co ton tai trong element.getWord_target()
                    // va kiem tra element.getWord_target() co bat dau = newValue
                    if(element.getWord_target().toUpperCase().contains(trimWord(newValue).toUpperCase())
                            && element.getWord_target().toUpperCase().indexOf(trimWord(newValue).toUpperCase()) == 0) {
                        wordList.getItems().add(element.getWord_target());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        // xu ly event: nhan chuot vao tu vung duoc goi y
        wordList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                        for (Word w: DictionaryManagement.getDictionary().getWords()) {
                            if (newValue.equals(w.getWord_target())) {
                                inputWord.setText(w.getWord_target());
                                wordTarget.setText(w.getWord_target());
                                wordExplain.setText(w.getWord_explain());
                            }
                        }
                    }
                }
        );

        // nut nhan -> phat am tu vung
        speek.setOnMouseClicked(mouseEvent -> {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            Voice voice = VoiceManager.getInstance().getVoice("kevin");

            String inputWordText = trimWord(wordTarget.getText());

            if(voice != null) {
                voice.allocate();
                voice.setPitch(90);
                voice.speak(inputWordText);
                voice.deallocate();
            }
        });
    }

    // loai bo tat ca cac khoang trang thua
    public static String trimWord(String str) {
        return str.replaceAll("\\s\\s+", " ").trim();
    }

    public void searchWord(ActionEvent actionEvent) {
        String inputWordText = trimWord(inputWord.getText());

        int dictionarySize = DictionaryManagement.getDictionary().getWords().size();
        Dictionary wordList = DictionaryManagement.getDictionary();

        // tim vi tri cua inputWordText trong tu dien
        int indexWord = Dictionary.binarySearch(wordList, 0, dictionarySize - 1, inputWordText);

        if (indexWord == -1) {
            wordTarget.setText(inputWordText);
            wordExplain.setText("Xin lỗi, chúng tôi không thể tìm thấy từ này !!!");
        } else {
            Word element = wordList.getWords().get(indexWord);
            wordTarget.setText(element.getWord_target());
            wordExplain.setText(element.getWord_explain());
        }
    }

    public void showEdit(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("FXML/editUI.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Từ điển Anh - Việt - Chỉnh sửa từ điển");
        stage.setScene(scene);
        stage.show();
    }

    public void showHelp(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("FXML/helpUI.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Từ điển Anh - Việt - Hướng dẫn sử dụng");
        stage.setScene(scene);
        stage.show();
    }
}