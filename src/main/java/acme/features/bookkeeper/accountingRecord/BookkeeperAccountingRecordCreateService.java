
package acme.features.bookkeeper.accountingRecord;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.accountingRecord.AccountingRecord;
import acme.entities.investmentRounds.InvestmentRound;
import acme.entities.roles.Bookkeeper;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractCreateService;

@Service
public class BookkeeperAccountingRecordCreateService implements AbstractCreateService<Bookkeeper, AccountingRecord> {

	@Autowired
	BookkeeperAccountingRecordRepository repository;


	@Override
	public boolean authorise(final Request<AccountingRecord> request) {
		assert request != null;

		int investId = request.getModel().getInteger("investId");
		boolean result = !this.repository.findExistsAccountingRecordByBookkeeperIdInvestmentRoundId(request.getPrincipal().getActiveRoleId(), investId);
		return result;
	}

	@Override
	public void bind(final Request<AccountingRecord> request, final AccountingRecord entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors, "creationMoment");
	}

	@Override
	public void unbind(final Request<AccountingRecord> request, final AccountingRecord entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		model.setAttribute("investId", request.getModel().getInteger("investId"));
		request.unbind(entity, model, "title", "status", "body");
	}

	@Override
	public AccountingRecord instantiate(final Request<AccountingRecord> request) {
		assert request != null;

		AccountingRecord result = new AccountingRecord();

		Principal principal = request.getPrincipal();
		Bookkeeper bookkeeper = this.repository.findBookkeeperById(principal.getActiveRoleId());
		result.setBookkeeper(bookkeeper);

		int investId = request.getModel().getInteger("investId");
		InvestmentRound invest = this.repository.findInvestmentRoundById(investId);
		result.setInvestmentRound(invest);

		return result;
	}

	@Override
	public void validate(final Request<AccountingRecord> request, final AccountingRecord entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

	}

	@Override
	public void create(final Request<AccountingRecord> request, final AccountingRecord entity) {
		assert request != null;
		assert entity != null;

		entity.setCreationMoment(new Date(System.currentTimeMillis() - 1));
		this.repository.save(entity);
	}

}
