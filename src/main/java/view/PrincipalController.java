package view;

import static config.Config.ALTERAR;
import static config.Config.EXCLUIR;
import static config.Config.INCLUIR;
import static config.DAO.clienteRepository;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Cliente;
import org.springframework.data.domain.Sort;
import utility.XPopOver;

public class PrincipalController implements Initializable {

    public char acao;
    public Cliente cliente;
    @FXML
    public TableView<Cliente> tblViewClientes;
    @FXML
    private TextField txtFldPesquisar;
    @FXML
    private MenuItem mnAlterar;
    @FXML
    private MenuItem mnAlterarPagar;
    @FXML
    private MenuItem mnPesquisar;
    @FXML
    private MenuItem mnExcluir;
    @FXML
    private MenuItem mnContas;
    @FXML
    private MaterialDesignIconView btnPesquisar;
    @FXML
    private MaterialDesignIconView btnAlterar;

    @FXML
    private void acIncluir() {
        acao = INCLUIR;
        cliente = new Cliente();
        showCRUD();
    }

    @FXML
    private void acAlterar() {
        acao = ALTERAR;
        cliente = tblViewClientes.getSelectionModel().getSelectedItem();
        showCRUD();
    }

    @FXML
    private void acExcluir() {
        acao = EXCLUIR;
        cliente = tblViewClientes.getSelectionModel().getSelectedItem();
        showCRUD();
    }

    /**
     * Mostra a janela modal CRUDCLiente
     */
    private void showCRUD() {
        String cena = "/fxml/CRUDCliente.fxml";
        XPopOver popOver = null;

        switch (acao) {
            case INCLUIR:
                popOver = new XPopOver(cena, "Inclusão de Cliente", null);
                break;
            case ALTERAR:
                popOver = new XPopOver(cena, "Alteração de Cliente", null);
                break;
            case EXCLUIR:
                popOver = new XPopOver(cena, "Exclusão de Cliente", null);
                break;
        }
        CRUDClienteController controllerFilho = popOver.getLoader().getController();
        controllerFilho.setCadastroController(this);
    }

    /**
     * Inicia a janela modal para alteração de contas do cliente selecionado na
     * TableView
     */
    @FXML
    private void acContas() {
        cliente = tblViewClientes.getSelectionModel().getSelectedItem();
        XPopOver popOver = new XPopOver("/fxml/Conta.fxml", "Contas", null);
        ContaController controllerFilho = popOver.getLoader().getController();
        controllerFilho.setCadastroController(this);
    }

    /**
     * Pesquisa um cliente, por nome,Sobrenome,Documento Retorna Allert da
     * ExceptionInInitializerError caso ocorra perda de conexão com o banco
     */
    @FXML
    private void acPesquisar() {
        tblViewClientes.refresh();
        try {

            tblViewClientes.setItems(FXCollections.observableList(
                    clienteRepository.findByNomeLikeIgnoreCaseOrSobrenomeLikeIgnoreCaseOrDocumentoLikeIgnoreCase(txtFldPesquisar.getText(), txtFldPesquisar.getText(), txtFldPesquisar.getText())));
        } catch (ExceptionInInitializerError e) {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR, "Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                    + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.", ButtonType.CLOSE);
            alert.setTitle("Erro na Conexão com o Banco de Dados");
            alert.setHeaderText("Conexão");
            alert.showAndWait();
            System.out.println("Erro na conexão com o banco");
            System.exit(0);
        }

    }

    /**
     * /**
     * Limpa o txtFldPesquisar
     */
    @FXML
    private void acLimpar() {
        txtFldPesquisar.clear();
//        tblViewClientes.setItems(
//                FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
    }

    /**
     * Evento ao dar dois cliques em uma linha da TableView
     *
     * @param event
     */
    @FXML
    private void tblViewClientesClick(Event event) {
        MouseEvent me = null;
        if (event.getEventType() == MOUSE_CLICKED) {
            me = (MouseEvent) event;
            if (me.getClickCount() == 2 && tblViewClientes.getSelectionModel().getSelectedItem() != null) {
                acContas();
            }
        }
    }

    /**
     * Initializes the controller class. Desabilita e habilita o botao
     * Alterar,mnAlterar,mnContas,btnPesquisar quando todos os dados estiverem
     * preenchidos Adiciona eventos de teclas ENTER,ESCAPE,F1,F2,F3,F4 apenas
     * números
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Cliente> lstCli = new ArrayList<>();
        try {

            lstCli = clienteRepository.findAll(new Sort(new Sort.Order("nome")));
        } catch (ExceptionInInitializerError e) {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR, "Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                    + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.", ButtonType.CLOSE);
            alert.setTitle("Erro na Conexão com o Banco de Dados");
            alert.setHeaderText("Conexão");
            alert.showAndWait();
            System.out.println("Erro na conexão com o banco");
            System.exit(0);
        }

//        tblViewClientes.setItems(FXCollections.observableArrayList(lstCli));
        btnAlterar.visibleProperty().bind(
                Bindings.isEmpty((tblViewClientes.getSelectionModel().getSelectedItems())).not());
        mnAlterar.visibleProperty().bind(btnAlterar.visibleProperty());
        mnExcluir.visibleProperty().bind(btnAlterar.visibleProperty());
        mnContas.visibleProperty().bind(btnAlterar.visibleProperty());
        mnAlterarPagar.visibleProperty().bind(btnAlterar.visibleProperty());
        btnPesquisar.disableProperty().bind(txtFldPesquisar.textProperty().isEmpty());
        mnPesquisar.disableProperty().bind(btnPesquisar.disableProperty());
        txtFldPesquisar.setOnKeyPressed(k -> {
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F5 = new KeyCodeCombination(KeyCode.F5);
            if (F4.match(k)) {
                tblViewClientes.requestFocus();
            } else if (F1.match(k)) {
                acIncluir();
            } else if (ENTER.match(k) && !txtFldPesquisar.getText().isEmpty()) {
                acPesquisar();
            } else if (F5.match(k)) {
                acLimpar();
            }
        });

        tblViewClientes.setOnKeyPressed(k -> {
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F2 = new KeyCodeCombination(KeyCode.F2);
            final KeyCombination F3 = new KeyCodeCombination(KeyCode.F3);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination F5 = new KeyCodeCombination(KeyCode.F5);
            if (F2.match(k) && tblViewClientes.getSelectionModel().getSelectedItem() != null) {
                acAlterar();
            } else if (ENTER.match(k) && tblViewClientes.getSelectionModel().getSelectedItem() != null) {
                acContas();
            } else if (F3.match(k)) {
                txtFldPesquisar.requestFocus();
            } else if (F1.match(k)) {
                acIncluir();
            } else if (F5.match(k)) {
                acLimpar();
            }
        });

    }
}
