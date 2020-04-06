package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerServices;

public class SellerFormController implements Initializable{

	private Seller entity;
	
	@FXML
	private TextField txtId;
	
	private SellerServices service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerServices(SellerServices service) {
		this.service = service;
	}
	
	public void subscribDataChengeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		// Caso tenha esquecido de injetar dependência
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			
			// SALVANDO NO BANCO DE DADOS
			service.SaveOrUpdate(entity);
			notifyDataChangeListeners();
			
			// FECHAR JANELA
			Utils.currentStage(event).close();
			
		} 	catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} 	catch (DbException e) {
			Alerts.showAlert("Error saving objeect", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
		
	}

	private Seller getFormData() {
		
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "field can't be empty");
		}
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		obj.setName(txtName.getText());
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		// Fechar janela
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	
}
