package com.nbloi.cqrses.commonapi.enums;

public enum PaymentStatus {
    // Reference the status on SAP: https://help.sap.com/docs/SAP_ERP/142f8559883b4c11966ebfb99dd61164/4d936d2548846e73e10000000a15822b.html?version=6.05.latest

    // The system sets this database status if the new payment has already been created
    // but not all the required data has been entered,
    // meaning that the system has not yet made all checks on completeness of the data.
    NEW,

    // The system sets this database status if you have entered all necessary data.
    // The system has made all checks for completeness of the data and considers all entries to be correct.
    CREATED,

    // This system sets this database status if you want to park a payment in the Claims Management system (see Parking Payments ),
    // but have not yet saved.
    TO_BE_PARKED,

    // The system sets this database status once you have flagged a payment as To Be Parked and saved.
    PARKED,

    // The system sets this database status if the payment has successfully completed the release process
    // (see Release or Rejection of a Payment to Be Released )
    // and is available for transfer to the collections/disbursements system.
    TO_BE_POSTED,

    // The system sets this database status if the payment has been successfully forwarded to the collections/disbursements system.
    COMPLETED,

    // When payment is failed to proceed
    FAILED

}
