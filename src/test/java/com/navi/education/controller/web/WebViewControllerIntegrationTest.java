package com.navi.education.controller.web;

import com.navi.education.model.entity.AnnualCommitment;
import com.navi.education.model.entity.Donor;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.DonorRepository;
import com.navi.education.repository.EducationClassRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration des routes web : sécurité (auth + CSRF), handlers POST
 * des formulaires classes/donateurs et page historique des années financières.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WebViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private EducationClassRepository classRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AnnualCommitmentRepository commitmentRepository;

    private Long anyClassId() {
        return classRepository.findAll().get(0).getId();
    }

    private Long anyBranchId() {
        return branchRepository.findAll().get(0).getId();
    }

    private Long anyDonorId() {
        return donorRepository.findAll().get(0).getId();
    }

    // ─────────────────────────── Sécurité ───────────────────────────

    @Test
    void unauthenticated_isRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginPage_isPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    void authenticated_canReachDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
    }

    @Test
    @WithMockUser
    void postWithoutCsrf_isForbidden() throws Exception {
        mockMvc.perform(post("/donors/new")
                        .param("firstName", "بدون")
                        .param("lastName", "رمز")
                        .param("telephone", "22000000"))
                .andExpect(status().isForbidden());
    }

    // ─────────────────────── Création donateur ──────────────────────

    @Test
    @WithMockUser
    void createDonor_persistsAndRedirects() throws Exception {
        long before = donorRepository.count();

        mockMvc.perform(post("/donors/new").with(csrf())
                        .param("firstName", "متبرع")
                        .param("lastName", "اختبار")
                        .param("telephone", "22987654")
                        .param("email", "donor@test.mr")
                        .param("notes", "ملاحظة"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/donors/*"));

        org.junit.jupiter.api.Assertions.assertEquals(before + 1, donorRepository.count());
    }

    // ─────────────────────── Modification donateur ──────────────────

    @Test
    @WithMockUser
    void updateDonor_changesData() throws Exception {
        Long id = anyDonorId();

        mockMvc.perform(post("/donors/{id}/edit", id).with(csrf())
                        .param("firstName", "محدث")
                        .param("lastName", "اسم")
                        .param("telephone", "22111222"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/donors/" + id));

        Donor updated = donorRepository.findById(id).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("محدث", updated.getFirstName());
        org.junit.jupiter.api.Assertions.assertEquals("22111222", updated.getTelephone());
    }

    // ──────────────────────── Création classe ───────────────────────

    @Test
    @WithMockUser
    void createClass_persistsAndRedirects() throws Exception {
        long before = classRepository.count();

        mockMvc.perform(post("/classes/new").with(csrf())
                        .param("nameAr", "فصل اختبار")
                        .param("branchId", String.valueOf(anyBranchId()))
                        .param("classType", "RECITATION")
                        .param("monthlyFixedAmount", "120000")
                        .param("startDate", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/classes/*"));

        org.junit.jupiter.api.Assertions.assertEquals(before + 1, classRepository.count());
    }

    // ─────────────────────── Modification classe ────────────────────

    @Test
    @WithMockUser
    void updateClass_changesData() throws Exception {
        Long id = anyClassId();

        mockMvc.perform(post("/classes/{id}/edit", id).with(csrf())
                        .param("nameAr", "فصل معدل")
                        .param("branchId", String.valueOf(anyBranchId()))
                        .param("classType", "PEDAGOGICAL")
                        .param("monthlyFixedAmount", "175000")
                        .param("startDate", "2024-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes/" + id));

        EducationClass updated = classRepository.findById(id).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("فصل معدل", updated.getNameAr());
    }

    // ───────────────────── Années financières (vue) ─────────────────

    @Test
    @WithMockUser
    void financialYearsPage_rendersWithModel() throws Exception {
        mockMvc.perform(get("/classes/{id}/years", anyClassId()))
                .andExpect(status().isOk())
                .andExpect(view().name("classes/years"))
                .andExpect(model().attributeExists("class"))
                .andExpect(model().attributeExists("years"));
    }

    // ───────────────────────── Détail donateur (régression) ──────────────────────
    // Régression : th:onclick="|openPaymentModal(${commitment.id}, '${commitment.className}', ...)|"
    // violait la restriction Thymeleaf 3.1 sur les attributs d'événement (seuls nombres/booléens
    // sont autorisés) dès qu'un engagement non soldé était affiché → page entière en erreur.

    @Test
    @WithMockUser
    @Transactional
    void donorViewPage_withUnpaidCommitment_rendersWithoutError() throws Exception {
        AnnualCommitment unpaid = commitmentRepository.findAll().stream()
                .filter(c -> !c.isFullyPaid())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun engagement non soldé dans les données de test : impossible de couvrir ce cas"));
        Long donorId = unpaid.getDonor().getId();

        mockMvc.perform(get("/donors/{id}", donorId))
                .andExpect(status().isOk())
                .andExpect(view().name("donors/view"));
    }
}
