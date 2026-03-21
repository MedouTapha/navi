package com.navi.education.controller.web;

import com.navi.education.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

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
        return "redirect:/branches";
    }

    @GetMapping("/branches")
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("activePage", "branches");
        return "branches/list";
    }

    @GetMapping("/branches/{id}")
    public String viewBranch(@PathVariable Long id, Model model) {
        model.addAttribute("branch", branchService.getBranchFinancialSummary(id));
        model.addAttribute("classes", classService.getClassesByBranch(id));
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

    @GetMapping("/classes/{id}")
    public String viewClass(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("expenses", expenseService.getExpensesByClass(id));
        model.addAttribute("commitments", commitmentService.getCommitmentsByClass(id));
        model.addAttribute("activePage", "classes");
        return "classes/view";
    }

    @GetMapping("/classes/{id}/edit")
    public String editClass(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("activePage", "classes");
        return "classes/form";
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

    @GetMapping("/donors/{id}")
    public String viewDonor(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.getDonorById(id));
        model.addAttribute("commitments", commitmentService.getCommitmentsByDonor(id));
        model.addAttribute("payments", paymentService.getPaymentsByDonor(id));
        model.addAttribute("activePage", "donors");
        return "donors/view";
    }

    @GetMapping("/donors/{id}/edit")
    public String editDonor(@PathVariable Long id, Model model) {
        model.addAttribute("donor", donorService.getDonorById(id));
        model.addAttribute("activePage", "donors");
        return "donors/form";
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
    public String dashboard(@RequestParam(required = false) Integer year, Model model) {
        // Si aucune année n'est spécifiée, utiliser l'année courante
        if (year == null) {
            year = java.time.LocalDate.now().getYear();
        }

        model.addAttribute("dashboard", dashboardService.getDashboardSummary(year));
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
