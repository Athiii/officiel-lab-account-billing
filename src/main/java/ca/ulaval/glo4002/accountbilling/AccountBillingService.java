package ca.ulaval.glo4002.accountbilling;

import java.util.List;

public class AccountBillingService {

	public void cancelInvoiceAndRedistributeFunds(BillId id) {
		Bill bill = BillDAO.getInstance().findBill(id);
		if (bill == null) {
			throw new BillNotFoundException();
		}
		ClientId clientId = bill.getClientId();
		if (!bill.isCancelled()){
			bill.cancel();
		}

		BillDAO.getInstance().persist(bill);

		List<Allocation> allocations = bill.getAllocations();

		for (Allocation allocation : allocations) {
			List<Bill> bills = BillDAO.getInstance().findAllByClient(clientId); int amount = allocation.getAmount();
			for (Bill billInBills : bills) {
				if (bill != billInBills) {
					int remainingAmount = billInBills.getRemainingAmount();
					Allocation newAllocation;
					if (remainingAmount <= amount) { newAllocation = new Allocation(remainingAmount);
						amount -= remainingAmount;
					} else {
						newAllocation = new Allocation(amount);
						amount = 0;
					}

					billInBills.addAllocation(newAllocation);
						
					BillDAO.getInstance().persist(billInBills);
				}

				if (amount == 0) {
					break;
				}
			}
		}

	}
}
