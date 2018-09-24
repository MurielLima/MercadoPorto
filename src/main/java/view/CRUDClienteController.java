package view;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static config.Config.ALTERAR;
import static config.Config.INCLUIR;
import static config.DAO.clienteRepository;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import org.springframework.data.domain.Sort;

/**
 * FXML Controller class
 *
 * @author Muriel
 */
public class CRUDClienteController implements Initializable {

    public PrincipalController controllerPai;
    @FXML
    private TextField txtFldNome;
    @FXML
    private TextField txtFldSobrenome;
    @FXML
    private TextField txtFldDocumento;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btnConfirma;

    @FXML
    private void btnConfirmaClick() {
        System.out.println(controllerPai.cliente.getNome());
        controllerPai.cliente.setDocumento(txtFldDocumento.getText());
        controllerPai.cliente.setNome(txtFldNome.getText());
        controllerPai.cliente.setSobrenome(txtFldSobrenome.getText());
        controllerPai.cliente.setDataCadastro(LocalDate.now());
        try {
            switch (controllerPai.acao) {
                case INCLUIR:
                    clienteRepository.insert(controllerPai.cliente);
                    break;
                case ALTERAR:
                    clienteRepository.save(controllerPai.cliente);
                    break;

            }
//            controllerPai.tblViewClientes.setItems(
//                    FXCollections.observableList(clienteRepository.findAll(
//                            new Sort(new Sort.Order("nome")))));
            controllerPai.tblViewClientes.refresh();
            controllerPai.tblViewClientes.getSelectionModel().clearSelection();
            controllerPai.tblViewClientes.getSelectionModel().select(controllerPai.cliente);
            anchorPane.getScene().getWindow().hide();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Cadastro de Cliente");
            if (e.getMessage().contains("duplicate key")) {
                alert.setContentText("Código já cadastrado");
            } else {
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
        }
    }

    public void setCadastroController(PrincipalController controllerPai) {
        this.controllerPai = controllerPai;
        if (controllerPai.acao == ALTERAR) {
            txtFldNome.setText(controllerPai.cliente.getNome());
            txtFldSobrenome.setText(controllerPai.cliente.getSobrenome());
            txtFldDocumento.setText(controllerPai.cliente.getDocumento());
        }
    }

    @FXML
    private void btnCancelaClick() {
        anchorPane.getScene().getWindow().hide();
        controllerPai.tblViewClientes.requestFocus();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnConfirma.disableProperty().bind(txtFldNome.textProperty().isEmpty().or(txtFldSobrenome.textProperty().isEmpty()).or(txtFldDocumento.textProperty().isEmpty()));
        anchorPane.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            if (ESCAPE.match(k)) {
                btnCancelaClick();
            } else if (ENTER.match(k)&& !btnConfirma.disabledProperty().get()) {
                btnConfirmaClick();
            }
        });
        txtFldDocumento.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*?")) {
                        txtFldDocumento.setText(oldValue);
                    } else {
                        txtFldDocumento.setText(newValue);
                    }
                });
    }

}
