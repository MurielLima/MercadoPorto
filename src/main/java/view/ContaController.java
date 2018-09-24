package view;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static config.Config.ALTERAR;
import static config.Config.EXCLUIR;
import static config.Config.INCLUIR;
import static config.Config.nf;
import static config.Config.separadorDecimal;
import static config.DAO.clienteRepository;
import static config.DAO.contaRepository;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Conta;
import org.springframework.data.domain.Sort;

/**
 * FXML Controller class
 *
 * @author Muriel
 */
public class ContaController implements Initializable {

    private Conta conta;
    private char acao;
    @FXML
    private TableView<Conta> tblViewContas;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private PrincipalController controllerPai;
    @FXML
    private Label lblNome;
    @FXML
    private StackPane stackPane;
    @FXML
    private VBox vbox;
    @FXML
    private TextField txtFldPago;
    @FXML
    private TextField txtFldValor;
    @FXML
    private TextField txtFldObservacao;
    @FXML
    private TextField txtFldObservacaoStack;
    @FXML
    private Label lblValor;
    @FXML
    private Label lblDocumento;
    @FXML
    private Button btnAdicionar;
    @FXML
    private Button btnPagar;

    @FXML
    private void btnAdicionarClick() {
        try {
            conta = new Conta(controllerPai.cliente, nf.parse(txtFldValor.getText()).doubleValue(), txtFldObservacao.getText(), LocalDate.now());
        } catch (ParseException ex) {
        }
        txtFldObservacao.clear();
        txtFldValor.clear();
        acao = INCLUIR;
        salvar();
    }

    @FXML
    private void btnPagarClick() {
        conta = tblViewContas.getSelectionModel().getSelectedItem();
        try {
            if (nf.parse(txtFldPago.getText()).doubleValue() == (conta.getValor())) {
                acao = EXCLUIR;
                stackPane.setVisible(false);
                vbox.setVisible(true);
                tblViewContas.getSelectionModel().clearSelection();
                txtFldPago.clear();
                txtFldObservacaoStack.clear();
                lblValor.setText("");
                salvar();
            } else if (nf.parse(txtFldPago.getText()).doubleValue() > 0 && nf.parse(txtFldPago.getText()).doubleValue() <= conta.getValor()) {
                conta.setValor(conta.getValor() - nf.parse(txtFldPago.getText()).doubleValue());
                conta.setObservacao(txtFldObservacaoStack.getText());
                acao = ALTERAR;
                stackPane.setVisible(false);
                vbox.setVisible(true);
                tblViewContas.getSelectionModel().clearSelection();
                txtFldObservacaoStack.clear();
                txtFldPago.clear();
                lblValor.setText("");
                salvar();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erro");
                alert.setHeaderText("Pagamento");
                alert.setContentText("Pagamento Inconscistente! Verifique");
                alert.showAndWait();
            }
        } catch (ParseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Pagamento");
            alert.setContentText("Pagamento não foi possível! Tente novamente, "
                    + "se o problema persistir entre em contato com o administrador do sistema");
            alert.showAndWait();
        }
    }

    private void salvar() {
        try {
            switch (acao) {
                case INCLUIR:
                    contaRepository.insert(conta);
                    break;
                case ALTERAR:
                    contaRepository.save(conta);
                    break;
                case EXCLUIR:
                    contaRepository.delete(conta);
                    break;
            }
            tblViewContas.refresh();
            tblViewContas.setItems(
                    FXCollections.observableList(contaRepository.findByIdCliente(controllerPai.cliente)));
            controllerPai.tblViewClientes.refresh();
            controllerPai.tblViewClientes.setItems(
                    FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Cadastro de Conta");
            if (e.getMessage().contains("duplicate key")) {
                alert.setContentText("Código já cadastrado");
            } else {
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
        }
    }

    private void mostraConta() {
        stackPane.setVisible(true);
        vbox.setVisible(false);
        txtFldPago.requestFocus();
        conta = tblViewContas.getSelectionModel().getSelectedItem();
        lblValor.setText("R$ " + String.valueOf(conta.getValorFormat()));
        txtFldObservacaoStack.setText(conta.getObservacao());
    }

    @FXML
    private void btnNovaContaClick() {
        stackPane.setVisible(false);
        vbox.setVisible(true);
    }

    @FXML
    public void btnFecharClick() {
        anchorPane.getScene().getWindow().hide();
        controllerPai.tblViewClientes.requestFocus();
        controllerPai.tblViewClientes.refresh();
//        controllerPai.tblViewClientes.setItems(
//                    FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
    }

    public void setCadastroController(PrincipalController controllerPai) {
        this.controllerPai = controllerPai;
        tblViewContas.setItems(
                FXCollections.observableList(contaRepository.findByIdCliente(controllerPai.cliente)));
        lblNome.setText(controllerPai.cliente.getNome() + " " + controllerPai.cliente.getSobrenome());
        lblDocumento.setText(controllerPai.cliente.getDocumento());
        stackPane.setVisible(false);
        vbox.setVisible(true);
        txtFldValor.requestFocus();
    }

    @FXML
    private void tblViewContasClick(Event event) {
        MouseEvent me = null;
        if (event.getEventType() == MOUSE_CLICKED) {
            me = (MouseEvent) event;
            if (me.getClickCount() == 2 && tblViewContas.getSelectionModel().getSelectedItem() != null) {
                mostraConta();
            }
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnAdicionar.disableProperty().bind(txtFldValor.textProperty().isEmpty().or(txtFldObservacao.textProperty().isEmpty()));
        btnPagar.disableProperty().bind(txtFldPago.textProperty().isEmpty());
        
        tblViewContas.setOnKeyPressed((KeyEvent k) -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (F3.match(k)) {
                btnNovaContaClick();
            } else if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if (ENTER.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && (!"".equals(txtFldPago.getText()) && "".equals(txtFldValor.getText()))) {
                btnPagarClick();
            } else if (ENTER.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && ("".equals(txtFldPago.getText()) && !"".equals(txtFldValor.getText()))) {
                btnAdicionarClick();
            } else if (ENTER.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null) {
                mostraConta();
            } else if (F1.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && !"".equals(txtFldValor.getText())) {
                btnAdicionarClick();
            } else if (F2.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && !"".equals(txtFldPago.getText())) {
                btnPagarClick();
            }
        });
        txtFldPago.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (F3.match(k)) {
                btnNovaContaClick();
            } else if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if ((ENTER.match(k) || F2.match(k)) && tblViewContas.getSelectionModel().getSelectedItem() != null && !"".equals(txtFldPago.getText())) {
                btnPagarClick();
            }
        });
        btnPagar.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (F3.match(k)) {
                btnNovaContaClick();
            } else if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if (ENTER.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && !"".equals(txtFldPago.getText())) {
                btnPagarClick();
            } else if (F2.match(k) && tblViewContas.getSelectionModel().getSelectedItem() != null && !"".equals(txtFldPago.getText())) {
                btnPagarClick();
            }
        });
        txtFldValor.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if (ENTER.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            } else if (F1.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            }
        });
        txtFldObservacao.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if (ENTER.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            } else if (F1.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            }
        });
        btnAdicionar.setOnKeyPressed(k -> {
            final KeyCombination ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            if (ESCAPE.match(k) || F4.match(k)) {
                btnFecharClick();
            } else if (ENTER.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            } else if (F1.match(k) && !"".equals(txtFldValor.getText()) && !"".equals(txtFldObservacao.getText())) {
                btnAdicionarClick();
            }
        });

        txtFldPago.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\" + separadorDecimal + "\\d*)?")) {
                        txtFldPago.setText(oldValue);
                    } else {
                        txtFldPago.setText(newValue);
                    }
                });
        txtFldValor.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*(\\" + separadorDecimal + "\\d*)?")) {
                        txtFldValor.setText(oldValue);
                    } else {
                        txtFldValor.setText(newValue);
                    }
                });

    }

}
