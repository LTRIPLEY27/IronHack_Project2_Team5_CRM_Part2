package com.ironhack.team5crm.services;

import com.ironhack.team5crm.models.SalesRep;
import com.ironhack.team5crm.repositories.*;
import com.ironhack.team5crm.services.exceptions.DataNotFoundException;
import com.ironhack.team5crm.models.Account;
import com.ironhack.team5crm.models.Lead;
import com.ironhack.team5crm.models.enums.Industry;
import com.ironhack.team5crm.models.enums.Product;
import com.ironhack.team5crm.models.enums.Status;
import com.ironhack.team5crm.models.exceptions.Team5CrmException;
import com.ironhack.team5crm.services.exceptions.EmptyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeadServiceImpleTest {

    @Autowired
    LeadRepository leadRepo;

    @Autowired
    OpportunityRepository opportunityRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    AccountRepository accountRepo;

    @Autowired
    SalesRepRepository salesRepRepository;

    @Autowired
    LeadServiceImple leadServiceImple;

    SalesRep salesRep;
    Lead lead1;
    Lead lead2;

    @BeforeEach
    void setUp() {
        leadRepo.deleteAll();
        opportunityRepository.deleteAll();
        contactRepository.deleteAll();
        accountRepo.deleteAll();
        salesRepRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        leadRepo.deleteAll();
        opportunityRepository.deleteAll();
        contactRepository.deleteAll();
        accountRepo.deleteAll();
        salesRepRepository.deleteAll();
    }

    @Test
    void test_newLead() {
        var salesRep = new SalesRep("test");
        salesRep = salesRepRepository.save(salesRep);
        var lead = new Lead("test", "666666666", "test@gmail.com", "company", salesRep);
        var leadCreated = leadServiceImple.newLead(lead.getName(), lead.getPhoneNumber(), lead.getEmail(),
                lead.getCompanyName(), salesRep);
        assertEquals(lead.getName(), leadCreated.getName());
        assertEquals(lead.getPhoneNumber(), leadCreated.getPhoneNumber());
        assertEquals(lead.getEmail(), leadCreated.getEmail());
        assertEquals(lead.getCompanyName(), leadCreated.getCompanyName());
    }

    @Test
    void test_convert() {
        addLeadsToDatasource();
        var product = Product.HYBRID;
        var prodQty = 5;
        var industry = Industry.MANUFACTURING;
        var emp = 60;
        var city = "BCN";
        var country = "Spain";

        var preFabAccount = new Account(industry, emp, city, country);
        var account = leadServiceImple.convert(this.lead1, product, prodQty, preFabAccount);

        // test account is saved
        assertNotNull(account.getId());
        assertFalse(account.getContactList().isEmpty());
        assertFalse(account.getOpportunityList().isEmpty());
        assertEquals(industry, account.getIndustry());
        assertEquals(emp, account.getEmployeesCount());
        assertEquals(city, account.getCity());
        assertEquals(country, account.getCountry());

        // test opportunity is created as expected and added to the account
        var oppCreated = account.getOpportunityList().get(0);
        assertNotNull(oppCreated);
        assertNotNull(oppCreated.getId());
        assertNotNull(oppCreated.getDecisionMaker());
        assertNotNull(oppCreated.getDecisionMaker().getId());
        assertEquals(this.lead1.getName(), oppCreated.getDecisionMaker().getName());
        assertEquals(this.lead1.getPhoneNumber(), oppCreated.getDecisionMaker().getPhoneNumber());
        assertEquals(this.lead1.getEmail(), oppCreated.getDecisionMaker().getEmail());
        assertEquals(oppCreated.getAccount().getId(), account.getId());
        assertEquals(account.getContactList().get(0), oppCreated.getDecisionMaker());
        assertEquals(product, oppCreated.getProduct());
        assertEquals(prodQty, oppCreated.getQuantity());
        assertEquals(Status.OPEN, oppCreated.getStatus());

        // test contact is created as expected and added to the account
        var contactCreated = account.getContactList().get(0);
        assertNotNull(contactCreated);
        assertEquals(this.lead1.getName(), contactCreated.getName());
        assertEquals(this.lead1.getPhoneNumber(), contactCreated.getPhoneNumber());
        assertEquals(this.lead1.getEmail(), contactCreated.getEmail());
        assertEquals(contactCreated.getAccount().getId(), account.getId());
    }

    @Test
    void test_getAllLeads() {
        addLeadsToDatasource();

        Team5CrmException exception = null;
        try {
            var leads = leadServiceImple.getAll();
            assertEquals(2, leads.size());
        } catch (EmptyException e) {
            exception = e;
        }
        assertNull(exception);

    }

    @Test
    void test_getAllLeads_shouldThrowIfNoLeadsAdded() {
        assertThrowsExactly(EmptyException.class, () -> leadServiceImple.getAll());
    }

    @Test
    void test_lookUpLead() {

        addLeadsToDatasource();

        Team5CrmException exception = null;
        try {
            var leadFound = leadServiceImple.lookUpLead(this.lead2.getId());
            assertEquals(this.lead2.getId(), leadFound.getId());
            assertEquals(this.lead2.getName(), leadFound.getName());
            assertEquals(this.lead2.getPhoneNumber(), leadFound.getPhoneNumber());
            assertEquals(this.lead2.getEmail(), leadFound.getEmail());
            assertEquals(this.lead2.getCompanyName(), leadFound.getCompanyName());
        } catch (EmptyException e) {
            exception = e;
        } catch (DataNotFoundException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    void test_lookUpLead_shouldThrowIfNoLeadsAdded() {
        assertThrowsExactly(EmptyException.class, () -> leadServiceImple.lookUpLead(5));
    }

    private void addLeadsToDatasource() {
        var salesRep = new SalesRep("test");
        salesRep = salesRepRepository.save(salesRep);
        lead1 = leadServiceImple.newLead("lead 1", "111111111", "lead1@gmail.com", "company 1", salesRep);
        lead2 = leadServiceImple.newLead("lead 2", "222222222", "lead2@hotmail.com", "company inc 2", salesRep);
    }
}
