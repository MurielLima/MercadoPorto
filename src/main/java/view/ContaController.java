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
import static config.DAO.contaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javafx.scene.control.ButtonType;
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

    /**
     * Instancia um objeto do tipo Conta com todos os valores recuperados da
     * interface Limpa os campos Observação e Valor da interface Adicionar Conta
     */
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

    /**
     * Deixa visivel o StackPane reponsavel pelo pagamento da conta Recebe os
     * valores da interface e verifica se o valor é > 0 && menor == ao valor da
     * conta Se o valor for menor que o da conta, ele seta para conta o valor da
     * conta subtraido do valor pago Se o valor for == ao valor da conta, ele
     * exclui a conta do banco de dados Se o valor não estiver dentro do range
     * [0,valor] ele retorna um Alert com 'erro de pagamento inconsistente'
     * Limpa os campos da interface e Deixa invisivel o StackPane devolvendo a
     * visibilidade ao AdicionarConta Retorna Alert de ParseException com a
     * informação de erro para verificar se alguma operação não foi possivel
     */
    @FXML
    private void btnPagarClick() {
        conta = tblViewContas.getSelectionModel().getSelectedItem();
        try {
//            if((new BigDecimal(nf.parse(txtFldPago.getText()).doubleValue()).setScale(2, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.ZERO) > 0)
            if (nf.parse(txtFldPago.getText()).doubleValue() == (conta.getValor().doubleValue())) {
                acao = EXCLUIR;
                stackPane.setVisible(false);
                vbox.setVisible(true);
                tblViewContas.getSelectionModel().clearSelection();
                txtFldPago.clear();
                txtFldObservacaoStack.clear();
                lblValor.setText("");
                salvar();
            } else if ((new BigDecimal(nf.parse(txtFldPago.getText()).doubleValue()).setScale(2, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.ZERO) > 0
                    && ((new BigDecimal(nf.parse(txtFldPago.getText()).doubleValue()).setScale(2, RoundingMode.HALF_EVEN)).compareTo(conta.getValor()) < 0)
                    || (new BigDecimal(nf.parse(txtFldPago.getText()).doubleValue()).setScale(2, RoundingMode.HALF_EVEN)).compareTo(conta.getValor()) == 0) {
//            } else if (nf.parse(txtFldPago.getText()) > 0 && nf.parse(txtFldPago.getText()) <= conta.getValor()) {

                conta.setValor(conta.getValor().subtract((new BigDecimal(nf.parse(txtFldPago.getText()).doubleValue()).setScale(2, RoundingMode.HALF_EVEN))).setScale(2, RoundingMode.HALF_EVEN).doubleValue());
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

    /**
     * Reponsavel por INSERIR,ALTERAR,EXCLUIR contas do banco de dados Retorna
     * um Alert da Exception caso a conexão com o banco de dados tenha sido
     * perdida ou algo tenha dado errado Retorna um Alert da Exception caso a
     * tenha dado erro de chave duplicada
     */
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
            try {

                tblViewContas.setItems(
                        FXCollections.observableList(contaRepository.findByIdCliente(controllerPai.cliente)));
            } catch (Exception e) {
                Alert alert;
                alert = new Alert(Alert.AlertType.ERROR, "Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                        + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.", ButtonType.CLOSE);
                alert.setTitle("Erro na Conexão com o Banco de Dados");
                alert.setHeaderText("Conexão");
                alert.showAndWait();
                System.out.println("Erro na conexão com o banco");
                btnFecharClick();
            }

//            controllerPai.tblViewClientes.refresh();
//            controllerPai.tblViewClientes.setItems(
//                    FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
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

    /**
     * Insere os valores da conta do banco de dados nos campos da interface
     * StackPane
     */
    private void mostraConta() {
        stackPane.setVisible(true);
        vbox.setVisible(false);
        txtFldPago.requestFocus();
        conta = tblViewContas.getSelectionModel().getSelectedItem();
        lblValor.setText("R$ " + String.valueOf(conta.getValorFormat()));
        txtFldObservacaoStack.setText(conta.getObservacao());
    }

    /**
     * Mostra a interface para adicionar Nova Conta e deixa invisivel a
     * interface Pagar conta
     */
    @FXML
    private void btnNovaContaClick() {
        stackPane.setVisible(false);
        vbox.setVisible(true);
    }

    /**
     * Fecha a janela e retorna para a janela controllerPai
     */
    @FXML
    public void btnFecharClick() {
        anchorPane.getScene().getWindow().hide();
        controllerPai.tblViewClientes.requestFocus();
//        controllerPai.tblViewClientes.refresh();
//        controllerPai.tblViewClientes.setItems(
//                    FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
    }

    /**
     * Executado pela classe PrincipalController, setCadastroControler cria um
     * node entre a controllerPai e this classe
     *
     * @param controllerPai
     */
    public void setCadastroController(PrincipalController controllerPai) {
        this.controllerPai = controllerPai;
        try {

            tblViewContas.setItems(
                    FXCollections.observableList(contaRepository.findByIdCliente(controllerPai.cliente)));
        } catch (Exception e) {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR, "Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                    + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.", ButtonType.CLOSE);
            alert.setTitle("Erro na Conexão com o Banco de Dados");
            alert.setHeaderText("Conexão");
            alert.showAndWait();
            System.out.println("Erro na conexão com o banco");
            btnFecharClick();
        }

        lblNome.setText(controllerPai.cliente.getNome() + " " + controllerPai.cliente.getSobrenome());
        lblDocumento.setText(controllerPai.cliente.getDocumento());
        stackPane.setVisible(false);
        vbox.setVisible(true);
        txtFldValor.requestFocus();
    }

    /**
     * Evento ao dar dois cliques em uma linha da TableView
     *
     * @param event
     */
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
     * Initializes the controller class. Desabilita e habilita o botao adicionar
     * e Pagar quando todos os dados estiverem preenchidos Adiciona eventos de
     * teclas ENTER,ESCAPE,F1,F2,F3,F4
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
