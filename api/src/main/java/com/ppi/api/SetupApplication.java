package com.ppi.api;

import com.ppi.api.model.*;
import com.ppi.api.service.OrganizationService;
import com.ppi.api.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Application
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@SpringBootApplication(exclude = HazelcastJpaDependencyAutoConfiguration.class)
public class SetupApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SetupApplication.class, args);
        OrganizationService organizationService = applicationContext.getBean(OrganizationService.class);
        UserService userService = applicationContext.getBean(UserService.class);
        Organization organization = new Organization();
        organization.setName("Nestup");

        NestupUser user1 = buildUser("Jason", "Carreira", 42, 68, 119032, "abc123", "jcarreira@gmail.com", new Role[]{Role.NESTUP_ADMIN, Role.COMPANY_ADMIN, Role.AUTHENTICATED_USER});
        Account[] accounts1 = {buildAccount(user1, "401k", 75000, 8500, 100, 0, 0, AccountType._401K),
                                buildAccount(user1, "HSA", 4000, 200, 30, 0, 70, AccountType.HSA)};
        user1.setAccounts(Arrays.asList(accounts1));

        NestupUser user2 = buildUser("Bob", "Dobbs", 45, 67, 55000, "abc123", "bob@dobbs.com", new Role[]{Role.COMPANY_ADMIN, Role.AUTHENTICATED_USER});
        Account[] accounts2 = {buildAccount(user2, "401k", 45000, 4500, 100, 0, 0, AccountType._401K),
                buildAccount(user2, "HSA", 2500, 100, 20, 10, 70, AccountType.HSA)};
        user2.setAccounts(Arrays.asList(accounts2));

        NestupUser[] nestupUsers = {user1, user2};
        organization.setUsers(new HashSet<>(Arrays.asList(nestupUsers)));

        organizationService.doCreate(organization);
        userService.doCreate(user1);
        userService.doCreate(user2);
    }

    static NestupUser buildUser(String firstName, String lastName, int age, int retirementAge, double salary, String password, String email, Role[] roles) {
        NestupUser user = new NestupUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setRetirementAge(retirementAge);
        user.setPassword(password);
        user.setEmail(email);
        user.setSalary(salary);
        user.setRoles(new HashSet<>(Arrays.asList(roles)));
        return user;
    }

    static Account buildAccount(NestupUser user, String name, int balance, int contribution, int stockPct, int bondPct, int cashPct, AccountType accountType) {
        Account account = new Account();
        account.setName(name);
        account.setBalance(balance);
        account.setContribution(contribution);
        account.setBondPercentage(bondPct);
        account.setCashPercentage(cashPct);
        account.setStockPercentage(stockPct);
        account.setType(accountType);
        account.setOwner(user);
        return account;
    }
}
