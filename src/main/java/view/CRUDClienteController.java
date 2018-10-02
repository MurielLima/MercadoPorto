package view;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static config.Config.ALTERAR;
import static config.Config.EXCLUIR;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

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

    /**
     * Botão de confirmação da operação Após ser clicado ele recupera todos os
     * dados da interface e insere na instancia da classe Passivel tanto para
     * atualização quanto para inclusão Retorna Alert da Exception se a conexão
     * com o banco for recusada e se a nova chave a ser inserida for duplicada
     *
     */
    @FXML
    private void btnConfirmaClick() {
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
                case EXCLUIR:
                    clienteRepository.delete(controllerPai.cliente);
                    break;
            }
            controllerPai.tblViewClientes.setItems(
                    FXCollections.observableList(clienteRepository.findByNomeLikeIgnoreCase(controllerPai.cliente.getNome())));
            controllerPai.tblViewClientes.refresh();
            controllerPai.tblViewClientes.requestFocus();
            controllerPai.tblViewClientes.getSelectionModel().clearSelection();
            controllerPai.tblViewClientes.getSelectionModel().select(controllerPai.cliente);
            anchorPane.getScene().getWindow().hide();
        } catch (Exception e) {
            if (e.getMessage().contains("duplicate key")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Cadastro de Cliente");
                alert.setContentText("Código já cadastrado");
                alert.showAndWait();
            } else {
                Alert alert;
                alert = new Alert(Alert.AlertType.ERROR, "Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                        + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.", ButtonType.CLOSE);
                alert.setTitle("Erro na Conexão com o Banco de Dados");
                alert.setHeaderText("Conexão");
                alert.showAndWait();
                System.out.println("Erro na conexão com o banco");
                btnCancelaClick();
            }

        }
    }

    /**
     * Executado pela classe PrincipalController setCadastroControler cria um
     * node entre a controllerPai e this classe
     *
     * @param controllerPai
     */
    public void setCadastroController(PrincipalController controllerPai) {
        this.controllerPai = controllerPai;
        if (controllerPai.acao == ALTERAR || controllerPai.acao == EXCLUIR) {
            txtFldNome.setText(controllerPai.cliente.getNome());
            txtFldSobrenome.setText(controllerPai.cliente.getSobrenome());
            txtFldDocumento.setText(controllerPai.cliente.getDocumento());
        }

        txtFldNome.setDisable(controllerPai.acao == EXCLUIR);
        txtFldSobrenome.setDisable(controllerPai.acao == EXCLUIR);
        txtFldDocumento.setDisable(controllerPai.acao == EXCLUIR);

    }

    /**
     * Cancela a operação e retorna a janela controllerPai
     */
    @FXML
    private void btnCancelaClick() {
        anchorPane.getScene().getWindow().hide();
        controllerPai.tblViewClientes.requestFocus();
    }

    /**
     * Initializes the controller class. Desabilita e habilita o botao Confirma
     * quando todos os dados estiverem preenchidos Adiciona eventos de teclas
     * ENTER,ESCAPE Adiciona Listener ao txtFldDocumento para serem inseridos
     * apenas números
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnConfirma.disableProperty().bind(txtFldNome.textProperty().isEmpty().or(txtFldSobrenome.textProperty().isEmpty()).or(txtFldDocumento.textProperty().isEmpty()));

        anchorPane.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            if (ESCAPE.match(k)) {
                btnCancelaClick();
            } else if (ENTER.match(k) && !btnConfirma.disabledProperty().get()) {
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
