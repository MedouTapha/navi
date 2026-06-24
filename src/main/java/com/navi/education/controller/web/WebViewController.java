package com.navi.education.controller.web;

import com.navi.education.dto.request.DonorRequest;
import com.navi.education.dto.request.EducationClassRequest;
import com.navi.education.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class WebViewController {

    private final BranchService branchService;
    private final EducationClassService classService;
    private final DonorService donorService;
    private final ExpenseService expenseService;
    private final AnnualCommitmentService commitmentService;
    private final PaymentService paymentService;
    private final DashboardService dashboardService;
    private final MonthlyReportService monthlyReportService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/branches")
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("activePage", "branches");
        return "branches/list";
    }

    @GetMapping("/branches/{id}")
    public String viewBranch(@PathVariable Long id,
                             @RequestParam(required = false) String date,
                             Model model) {
        LocalDate refDate = parseDate(date);
        model.addAttribute("branchSummary", dashboardService.getBranchClassSummaries(id, refDate));
        model.addAttribute("currentDate", refDate.toString());
        model.addAttribute("activePage", "branches");
        return "branches/view";
    }

    @GetMapping("/classes")
    public String listClasses(Model model) {
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("activePage", "classes");
        return "classes/list";
    }

    @GetMapping("/classes/new")
    public String newClass(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("activePage", "classes");
        return "classes/form";
    }

    @PostMapping("/classes/new")
    public String createClass(@ModelAttribute EducationClassRequest request) {
        request.setNameFr(request.getNameAr());
        var created = classService.createClass(request);
        return "redirect:/classes/" + created.getId();
    }

    @GetMapping("/classes/{id}")
    public String viewClass(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("expenses", expenseService.getExpensesByClass(id));
        model.addAttribute("commitments", commitmentService.getCommitmentsByClass(id));
        model.addAttribute("payments", paymentService.getPaymentsByClass(id));
        model.addAttribute("activePage", "classes");
        return "classes/view";
    }

    @GetMapping("/classes/{id}/years")
    public String classFinancialYears(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("years", classService.getFinancialYearsHistory(id));
        model.addAttribute("activePage", "classes");
        return "classes/years";
    }

    @GetMapping("/classes/{id}/edit")
    public String editClass(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("activePage", "classes");
        return "classes/form";
    }

    @PostMapping("/classes/{id}/edit")
    public String updateClass(@PathVariable Long id, @ModelAttribute EducationClassRequest request) {
        request.setNameFr(request.getNameAr());
        classService.updateClass(id, request);
        return "redirect:/classes/" + id;
    }

    @GetMapping("/donors")
    public String listDonors(Model model) {
        model.addAttribute("donors", donorService.getAllDonors());
        model.addAttribute("activePage", "donors");
        return "donors/list";
    }

    @GetMapping("/donors/new")
    public String newDonor(Model model) {
        model.addAttribute("activePage", "donors");
        return "donors/form";
    }

    @PostMapping("/donors/new")
    public String createDonor(@ModelAttribute DonorRequest request) {
        var created = donorService.createDonor(request);
        return "redirect:/donors/" + created.getId();
    }

    @GetMapping("/donors/{id}")
    public String viewDonor(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.getDonorById(id));
        model.addAttribute("commitments", commitmentService.getCommitmentsByDonor(id));
        model.addAttribute("payments", paymentService.getPaymentsByDonor(id));
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("activePage", "donors");
        return "donors/view";
    }

    @GetMapping("/donors/{id}/edit")
    public String editDonor(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.getDonorById(id));
        model.addAttribute("activePage", "donors");
        return "donors/form";
    }

    @PostMapping("/donors/{id}/edit")
    public String updateDonor(@PathVariable Long id, @ModelAttribute DonorRequest request) {
        donorService.updateDonor(id, request);
        return "redirect:/donors/" + id;
    }

    @GetMapping("/rapport-mensuel")
    public String monthlyReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        model.addAttribute("report", monthlyReportService.getMonthlyReport(year, month));
        model.addAttribute("currentYear", year);
        model.addAttribute("currentMonth", month);
        model.addAttribute("activePage", "rapport");
        return "rapport-mensuel";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String date, Model model) {
        LocalDate refDate = parseDate(date);
        model.addAttribute("dashboard", dashboardService.getDashboardSummary(refDate));
        model.addAttribute("currentDate", refDate.toString());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/retards")
    public String retards(Model model) {
        var unpaid = commitmentService.getAllUnpaidCommitments();
        var totalRemaining = unpaid.stream()
                .map(c -> c.getRemainingBalance())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("commitments", unpaid);
        model.addAttribute("totalRemaining", totalRemaining);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activePage", "retards");
        return "retards";
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                return LocalDate.parse(dateStr);
            } catch (Exception ignored) {
            }
        }
        return LocalDate.now();
    }
}
