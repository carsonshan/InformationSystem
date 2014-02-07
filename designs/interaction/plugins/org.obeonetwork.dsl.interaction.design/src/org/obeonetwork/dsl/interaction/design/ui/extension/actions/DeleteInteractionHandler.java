package org.obeonetwork.dsl.interaction.design.ui.extension.actions;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.obeonetwork.dsl.interaction.Interaction;

import fr.obeo.dsl.viewpoint.DRepresentation;
import fr.obeo.dsl.viewpoint.business.api.dialect.DialectManager;
import fr.obeo.dsl.viewpoint.business.api.session.Session;
import fr.obeo.dsl.viewpoint.business.api.session.SessionManager;
import fr.obeo.mda.ecore.extender.business.api.accessor.ModelAccessor;

public class DeleteInteractionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Interaction interaction = extractInteraction(event);
		if (interaction == null) {
			return null;
		}
		boolean confirm = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "Delete interaction", "Delete the selected interaction ?");
		if (confirm) {
			final Session session = SessionManager.INSTANCE.getSession(interaction);
			final ModelAccessor modelAccessor = session.getModelAccessor();
			final ECrossReferenceAdapter semanticCrossReferencer = session.getSemanticCrossReferencer();
			
			TransactionalEditingDomain transactionalEditingDomain = session.getTransactionalEditingDomain();
			transactionalEditingDomain.getCommandStack().execute(new RecordingCommand(transactionalEditingDomain) {
				@Override
				protected void doExecute() {
					// Retrieve associated representations
					Collection<DRepresentation> representations = DialectManager.INSTANCE.getRepresentations(interaction, session);
					// Delete representations
					for (DRepresentation representation : representations) {
						modelAccessor.eDelete(representation, semanticCrossReferencer);
					}
					// Delete interaction
					modelAccessor.eDelete(interaction, semanticCrossReferencer);
				}
			});
		}
		return null;
	}
	
	private Interaction extractInteraction(ExecutionEvent event) {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection)currentSelection).getFirstElement();
			if (selectedObject instanceof Interaction) {
				return (Interaction)selectedObject;
			}
		}
		return null;
	}

}
