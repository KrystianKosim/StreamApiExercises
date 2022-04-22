package zadaniaStreamApi;

import zadaniaStreamApi.generator.AccountGenerator;
import zadaniaStreamApi.generator.CompanyGenerator;
import zadaniaStreamApi.generator.HoldingGenerator;
import zadaniaStreamApi.generator.UserGenerator;
import zadaniaStreamApi.model.Currency;
import zadaniaStreamApi.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Exercises {

    private static final List<Holding> holdings = new HoldingGenerator().generate();
    private static final List<Account> accounts = new AccountGenerator().generate();
    private static final List<User> users = new UserGenerator().generate();
    private static final List<Company> companies = new CompanyGenerator().generate();

    public static void main(String[] args) {

        System.out.println("=======1");
        System.out.println(getHoldingsWhereAreCompanies());
        System.out.println("=======2");
        System.out.println(getHoldingNames());
        System.out.println("=======3");
        System.out.println(getHoldingNamesAsString());
        System.out.println("=======4");
        System.out.println(getCompaniesAmount());
        System.out.println("=======5");
        System.out.println(getAllUserAmount());
        System.out.println("=======6");
        System.out.println(getAllCompaniesNamesAsLinkedList());
        System.out.println("=======7");
        System.out.println(getAccountAmountInPLN(accounts.get(1)));
        System.out.println("=======8");
        Predicate<User> ex8 = (user) -> user.getFirstName().contains("z");
        System.out.println(getUsersForPredicate(ex8));
        System.out.println("=======9");
        Consumer<Company> ex9 = (company) -> System.out.println(company);
        executeForEachCompany(ex9);
        System.out.println("=======10");
        System.out.println(getRichestWoman().get());
        System.out.println("=======11");
        System.out.println(getUserAmountInPLN(users.get(4)));
        System.out.println("=======12");
        System.out.println(getFirstNCompany(3));
        System.out.println("=======13");
        System.out.println(getUserPerCompany());
        System.out.println("=======14");
        Predicate<User> ex14 = (user) -> user.getFirstName().contains("a");
        System.out.println(getUser(ex14));
        System.out.println("=======15");
        System.out.println(createAccountsMap());
        System.out.println("=======16");
        System.out.println(getUserNames());
        System.out.println("=======17");
        showAllUser();
        System.out.println("=======18");
        System.out.println(getCurenciesSet());
        System.out.println("=======19");
        System.out.println(getCompanyStream());
        System.out.println("=======20");
        System.out.println(getUserStream());
        System.out.println("=======21");
        System.out.println(getAccoutStream());
    }

    /**
     * Napisz metodę, która zwróci liczbę holdingów, w których jest przynajmniej jedna firma.
     */
    public static long getHoldingsWhereAreCompanies() {
        return holdings.stream()
                .filter(h -> h.getCompanies().size() > 0)
                .count();
    }

    /**
     * Napisz metodę, która zwróci nazwy wszystkich holdingów pisane z wielkiej litery w formie listy.
     */
    public static List<String> getHoldingNames() {
        return holdings.stream()
                .map(h -> h.getName().toUpperCase())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca nazwy wszystkich holdingów sklejone w jeden string i posortowane.
     * String ma postać: (Coca-Cola, Nestle, Pepsico)
     */
    public static String getHoldingNamesAsString() {
        return holdings.stream()
                .map(h -> h.getName())
                .sorted()
                .collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * Zwraca liczbę firm we wszystkich holdingach.
     */
    public static long getCompaniesAmount() {
        return holdings.stream()
                .flatMap(h -> h.getCompanies().stream())
                .count();
    }


    /**
     * Zwraca liczbę wszystkich pracowników we wszystkich firmach.
     */
    public static long getAllUserAmount() {
        return holdings.stream()
                .flatMap(h -> h.getCompanies().stream())
                .mapToLong(list -> list.getUsers().size())
                .sum();
    }

    /**
     * Zwraca listę wszystkich firm jako listę, której implementacja to LinkedList. Obiektów nie przepisujemy
     * po zakończeniu działania strumienia.
     */
    public static LinkedList<String> getAllCompaniesNamesAsLinkedList() {
        return companies.stream()
                .map(c -> c.toString())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Przelicza kwotę na rachunku na złotówki za pomocą kursu określonego w enum Currency.
     */
    public static BigDecimal getAccountAmountInPLN(Account account) {
        return account
                .getAmount()
                .multiply(BigDecimal.valueOf(account.getCurrency().rate));
    }

    /**
     * Zwraca imiona użytkowników w formie zbioru, którzy spełniają podany warunek.
     */
    public static Set<String> getUsersForPredicate(final Predicate<User> userPredicate) {
        return users.stream()
                .filter(u -> userPredicate.test(u))
                .map(u -> u.getFirstName())
                .collect(Collectors.toSet());
    }

    /**
     * Dla każdej firmy uruchamia przekazaną metodę.
     */
    public static void executeForEachCompany(Consumer<Company> consumer) {
        companies.stream()
                .forEach(c -> consumer.accept(c));
    }

    /**
     * Wyszukuje najbogatsza kobietę i zwraca ją. Metoda musi uzwględniać to że rachunki są w różnych walutach.
     */
    //pomoc w rozwiązaniu problemu w zadaniu: https://stackoverflow.com/a/55052733/9360524
    public static Optional<User> getRichestWoman() {
        return users.stream()
                .filter(u -> u.getSex() == Sex.WOMAN)
                .max(Comparator.comparing((u) -> getUserAmountInPLN(u)));
    }

    private static BigDecimal getUserAmountInPLN(final User user) {
        return user.getAccounts().stream()
                .map(u -> getAccountAmountInPLN(u))
                .reduce((u1, u2) -> u1.add(u2))
                .get();
    }

    /**
     * Zwraca nazwy pierwszych N firm. Kolejność nie ma znaczenia.
     */
    private static Set<String> getFirstNCompany(final int n) {
        return companies.stream()
                .map(c -> c.getName())
                .limit(n)
                .collect(Collectors.toSet());
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników.
     */
    public static Map<String, List<User>> getUserPerCompany() {
        return companies.stream()
                .collect(Collectors.toMap(c -> c.getName(), c -> c.getUsers()));
    }

    /**
     * Zwraca pierwszego z brzegu użytkownika dla podanego warunku. W przypadku kiedy nie znajdzie użytkownika, wyrzuca
     * wyjątek IllegalArgumentException.
     */
    public static User getUser(final Predicate<User> predicate) {
        return users.stream()
                .filter(u -> predicate.test(u))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
    }

    /**
     * Zwraca mapę rachunków, gdzie kluczem jest numer rachunku, a wartością ten rachunek.
     */
    public static Map<String, Account> createAccountsMap() {
        return accounts.stream()
                .collect(Collectors.toMap(a -> a.getNumber(), a -> a));
    }

    /**
     * Zwraca listę wszystkich imion w postaci Stringa, gdzie imiona oddzielone są spacją i nie zawierają powtórzeń.
     */
    public static String getUserNames() {
        return users.stream()
                .map(u -> u.getFirstName())
                .distinct()
                .collect(Collectors.joining(" "));
    }

    /**
     * Metoda wypisuje na ekranie wszystkich użytkowników (imię, nazwisko) posortowanych od z do a.
     * Zosia Psikuta, Zenon Kucowski, Zenek Jawowy ... Alfred Pasibrzuch, Adam Wojcik
     */
    public static void showAllUser() {
        users.stream()
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .sorted(Collections.reverseOrder())
                .forEach(System.out::println);
    }

    /**
     * Zwraca zbiór walut w jakich są rachunki.
     */
    public static Set<Currency> getCurenciesSet() {
        return accounts.stream()
                .map(u -> u.getCurrency())
                .collect(Collectors.toSet());
    }

    /**
     * Zwraca strumień wszystkich firm.
     */
    private static Stream<Company> getCompanyStream() {
        return holdings.stream()
                .flatMap(holding -> holding.getCompanies().stream());
    }

    /**
     * Tworzy strumień użytkowników.
     */
    private static Stream<User> getUserStream() {
        return getCompanyStream()
                .flatMap(company -> company.getUsers().stream());
    }

    /**
     * Tworzy strumień rachunków.
     */
    private static Stream<Account> getAccoutStream() {
        return getUserStream()
                .flatMap(user -> user.getAccounts().stream());
    }

}
