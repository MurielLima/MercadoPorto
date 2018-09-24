package view;

import com.mongodb.MongoException;
import static config.Config.ALTERAR;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
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
        }
        CRUDClienteController controllerFilho = popOver.getLoader().getController();
        controllerFilho.setCadastroController(this);
    }

    @FXML
    private void acContas() {
        cliente = tblViewClientes.getSelectionModel().getSelectedItem();
        XPopOver popOver = new XPopOver("/fxml/Conta.fxml", "Contas", null);
        ContaController controllerFilho = popOver.getLoader().getController();
        controllerFilho.setCadastroController(this);
    }

    @FXML
    private void acPesquisar() {
        tblViewClientes.refresh();
        tblViewClientes.setItems(FXCollections.observableList(
                clienteRepository.findByNomeLikeIgnoreCaseOrSobrenomeLikeIgnoreCaseOrDocumentoLikeIgnoreCase(txtFldPesquisar.getText(), txtFldPesquisar.getText(), txtFldPesquisar.getText())));
    }

    @FXML
    private void acLimpar() {
        txtFldPesquisar.clear();
//        tblViewClientes.setItems(
//                FXCollections.observableList(clienteRepository.findAll(new Sort(new Sort.Order("nome")))));
    }

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Cliente> lstCli = new ArrayList<>();
        try {

            lstCli = clienteRepository.findAll(new Sort(new Sort.Order("nome")));
        } catch (ExceptionInInitializerError e) {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR,"Desculpe, ocorreu um erro ao conectar com o banco, \r\n"
                    + "Verique se o serviço do Banco de Dados MongoDB está ativo e se o IP está correto.",ButtonType.CLOSE);
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
        mnContas.visibleProperty().bind(btnAlterar.visibleProperty());
        btnPesquisar.disableProperty().bind(txtFldPesquisar.textProperty().isEmpty());

        txtFldPesquisar.setOnKeyPressed(k -> {
            final KeyCombination F4 = new KeyCodeCombination(KeyCode.F4);
            final KeyCombination F1 = new KeyCodeCombination(KeyCode.F1);
            final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
            final KeyCombination F5 = new KeyCodeCombination(KeyCode.F5);
            if (F4.match(k)) {
                tblViewClientes.requestFocus();
            } else if (F1.match(k)) {
                acIncluir();
            } else if (ENTER.match(k)) {
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
