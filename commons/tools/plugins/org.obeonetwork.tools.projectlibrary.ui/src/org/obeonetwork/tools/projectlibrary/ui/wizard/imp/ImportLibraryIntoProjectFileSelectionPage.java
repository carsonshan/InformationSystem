/**
 * Copyright (c) 2017 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 */
package org.obeonetwork.tools.projectlibrary.ui.wizard.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sirius.business.api.modelingproject.ModelingProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.obeonetwork.dsl.manifest.MManifest;
import org.obeonetwork.dsl.manifest.util.ManifestUtils;
import org.obeonetwork.tools.projectlibrary.extension.ManifestServices;
import org.obeonetwork.tools.projectlibrary.ui.wizard.imp.ImportLibraryIntoProjectWizardModel.DependencyRow;

public class ImportLibraryIntoProjectFileSelectionPage extends WizardPage {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	
	private ImportLibraryIntoProjectWizard wizard;
	
	private ManifestServices manifestServices = new ManifestServices();
	
	private Text txtMarFile;
	private Text txtProjectId;
	private Text txtVersion;
	private Text txtComment;
	private Table table;
	private ComboViewer comboViewer;
	private Text txtCreationDate;

	/**
	 * Create the wizard.
	 */
	public ImportLibraryIntoProjectFileSelectionPage(ImportLibraryIntoProjectWizard wizard) {
		super("ImportLibraryIntoProjectFileSelectionPage");
		setTitle("Import a library into a modeling");
		setDescription("Select MAR file to import and modeling project");
		this.wizard = wizard;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label lblModelingProject = new Label(container, SWT.NONE);
		lblModelingProject.setText("Modeling project");
		
		comboViewer = new ComboViewer(container, SWT.NONE);
		comboViewer.setUseHashlookup(true);
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				computeDependenciesTable();
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblNewLabel.setText("MAR File");
		
		Label lblFileToImport = new Label(container, SWT.NONE);
		lblFileToImport.setText("Import file");
		
		txtMarFile = new Text(container, SWT.BORDER);
		txtMarFile.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(getShell());
				dlg.setFileName(txtMarFile.getText());
				dlg.setOverwrite(true);
				dlg.setFilterExtensions(new String[]{"*.mar", "*.*"});
				dlg.setFilterNames(new String[]{"MAR files (*.mar)", "All files (*.*)"});
				String importFile = dlg.open();
				if (importFile != null) {
					try {
						Manifest manifest = manifestServices.getManifestFromArchive(new File(importFile));
						extractInfoFromManifest(manifest);
					} catch (FileNotFoundException e1) {
						// TODO Error thrown
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Error thrown
						e1.printStackTrace();
					}
					
					txtMarFile.setText(importFile);
					setPageComplete(isInfoComplete());
				}
			}
		});
		btnBrowse.setText("Browse...");
		
		Label lblProjectId = new Label(container, SWT.NONE);
		lblProjectId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProjectId.setText("Project ID");
		
		txtProjectId = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtProjectId.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version");
		
		txtVersion = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblCreationDate = new Label(container, SWT.NONE);
		lblCreationDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCreationDate.setText("Creation date");
		
		txtCreationDate = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtCreationDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblComment = new Label(container, SWT.NONE);
		lblComment.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblComment.setText("Comment");
		
		txtComment = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		txtComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Label lblDependencies = new Label(container, SWT.NONE);
		lblDependencies.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblDependencies.setText("Dependencies");
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn tableViewerColumnId = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnProjectId = tableViewerColumnId.getColumn();
		tcl_composite.setColumnData(tblclmnProjectId, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnProjectId.setText("Project ID");
		tableViewerColumnId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DependencyRow) {
					return ((DependencyRow) element).getId();
				}
				return super.getText(element);
			}
		});
		
		TableViewerColumn tableViewerColumnVersion = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnVersion = tableViewerColumnVersion.getColumn();
		tcl_composite.setColumnData(tblclmnVersion, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnVersion.setText("Version");
		tableViewerColumnVersion.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DependencyRow) {
					return ((DependencyRow) element).getVersion();
				}
				return super.getText(element);
			}
		});
		
		TableViewerColumn tableViewerColumnExistingVersion = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnExistingVersion = tableViewerColumnExistingVersion.getColumn();
		tcl_composite.setColumnData(tblclmnExistingVersion, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnExistingVersion.setText("Existing version");
		tableViewerColumnExistingVersion.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DependencyRow) {
					return ((DependencyRow) element).getExistingVersion();
				}
				return super.getText(element);
			}
		});
		
		TableViewerColumn tableViewerColumnValid = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnValid = tableViewerColumnValid.getColumn();
		tcl_composite.setColumnData(tblclmnValid, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
		tblclmnValid.setText("Valid");
		tableViewerColumnValid.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getText(Object element) {
				// No text, we only want to display an image
				return null;
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof DependencyRow) {
					DependencyRow row = (DependencyRow) element;
					if (ManifestUtils.isGreaterOrEqual(row.getVersion(), row.getExistingVersion())) {
						return ResourceManager.getPluginImage("org.obeonetwork.tools.projectlibrary.ui", "icons/valid.gif");						
					} else {
						return ResourceManager.getPluginImage("org.obeonetwork.tools.projectlibrary.ui", "icons/invalid.gif");
					}
				}
				return super.getImage(element);
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		setPageComplete(isInfoComplete());
		
		m_bindingContext = initDataBindings();
	}
	
	private void extractInfoFromManifest(Manifest manifest) {
		if (manifest != null) {
			MManifest manifestModel = manifestServices.getModelFromMarManifest(manifest);
			
			txtProjectId.setText(manifestModel.getProjectId());
			txtVersion.setText(manifestModel.getVersion());
			txtComment.setText(manifestModel.getComment());
			if (manifestModel.getCreationDate() != null) {
				txtCreationDate.setText(DATE_FORMAT.format(manifestModel.getCreationDate()));			
			}
			wizard.getModel().getDependencies().addAll(manifestModel.getDependencies());
			wizard.getModel().setValidMarFile(true);
		} else {
			txtProjectId.setText(null);
			txtVersion.setText(null);
			txtComment.setText(null);
			txtCreationDate.setText(null);
			wizard.getModel().getDependencies().clear();
			wizard.getModel().getExistingDependenciesRows().clear();
			wizard.getModel().setValidMarFile(false);
		}
		computeDependenciesTable();
	}
	
	private void computeDependenciesTable() {
		List<DependencyRow> rows = new ArrayList<>();
		for (MManifest dependency : wizard.getModel().getDependencies()) {
			DependencyRow row = new DependencyRow();
			row.setId(dependency.getProjectId());
			row.setVersion(dependency.getVersion());
			String existingVersion = getExistingVersion(dependency.getProjectId());
			if (existingVersion != null) {
				row.setExistingVersion(existingVersion);
			}
			rows.add(row);
		}
		wizard.getModel().setExistingDependenciesRows(rows);
	}
	
	private String getExistingVersion(String projectId) {
		for (MManifest existingDep : wizard.getModel().getExistingDependencies()) {
			if (projectId.equals(existingDep.getProjectId())) {
				return existingDep.getVersion();
			}
		}
		return null;
	}
	
	private boolean isInfoComplete() {
		ModelingProject modelingProject = wizard.getModel().getModelingProject();
		String filePath  = wizard.getModel().getFilepath();
		return modelingProject != null && filePath != null && !filePath.trim().isEmpty() && wizard.getModel().isValidMarFile();
	}

	public ImportLibraryIntoProjectWizard getWizard() {
		return wizard;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = PojoObservables.observeMap(listContentProvider.getKnownElements(), ModelingProject.class, "project.name");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList modelingProjectsWizardgetModelObserveList = PojoProperties.list("modelingProjects").observe(wizard.getModel());
		comboViewer.setInput(modelingProjectsWizardgetModelObserveList);
		//
		IObservableValue observeTextTxtMarFileObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtMarFile);
		IObservableValue filepathWizardgetModelObserveValue = PojoProperties.value("filepath").observe(wizard.getModel());
		bindingContext.bindValue(observeTextTxtMarFileObserveWidget, filepathWizardgetModelObserveValue, null, null);
		//
		IObservableValue observeTextTxtProjectIdObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtProjectId);
		IObservableValue projectIDWizardgetModelObserveValue = PojoProperties.value("projectID").observe(wizard.getModel());
		bindingContext.bindValue(observeTextTxtProjectIdObserveWidget, projectIDWizardgetModelObserveValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextTxtVersionObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtVersion);
		IObservableValue versionWizardgetModelObserveValue = PojoProperties.value("version").observe(wizard.getModel());
		bindingContext.bindValue(observeTextTxtVersionObserveWidget, versionWizardgetModelObserveValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextTxtCommentObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtComment);
		IObservableValue commentWizardgetModelObserveValue = PojoProperties.value("comment").observe(wizard.getModel());
		bindingContext.bindValue(observeTextTxtCommentObserveWidget, commentWizardgetModelObserveValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeTextTxtCreationDateObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtCreationDate);
		IObservableValue creationDateWizardgetModelObserveValue = PojoProperties.value("creationDate").observe(wizard.getModel());
		bindingContext.bindValue(observeTextTxtCreationDateObserveWidget, creationDateWizardgetModelObserveValue, new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue modelingProjectWizardgetModelObserveValue = PojoProperties.value("modelingProject").observe(wizard.getModel());
		bindingContext.bindValue(observeSingleSelectionComboViewer, modelingProjectWizardgetModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
