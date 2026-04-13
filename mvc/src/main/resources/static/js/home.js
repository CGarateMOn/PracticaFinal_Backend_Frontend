const statusPill = document.getElementById("statusPill");
const sessionHeadline = document.getElementById("sessionHeadline");
const sessionSummary = document.getElementById("sessionSummary");
const availabilityForm = document.getElementById("availabilityForm");
const availabilityFeedback = document.getElementById("availabilityFeedback");
const availabilityResults = document.getElementById("availabilityResults");
const authModal = document.getElementById("authModal");
const closeModalButton = document.getElementById("closeModalButton");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");
const loginFeedback = document.getElementById("loginFeedback");
const registerFeedback = document.getElementById("registerFeedback");
const logoutButton = document.getElementById("logoutButton");
const logoutModalButton = document.getElementById("logoutModalButton");
const accountPanel = document.getElementById("accountPanel");
const profileName = document.getElementById("profileName");
const profileEmail = document.getElementById("profileEmail");
const profileRole = document.getElementById("profileRole");
const tabButtons = Array.from(document.querySelectorAll(".tab-button"));
const tabPanels = Array.from(document.querySelectorAll(".tab-panel"));
const authTriggers = Array.from(document.querySelectorAll("[data-open-auth]"));
const revealItems = Array.from(document.querySelectorAll("[data-reveal]"));
const isPreviewMode = window.location.port === "3000" || window.location.pathname.includes("/src/main/resources/templates/");
const previewAvailability = [
    {
        idPista: 1,
        fecha: new Date().toISOString().slice(0, 10),
        tramosHorariosDisponibles: [
            { inicio: "09:00:00", fin: "10:30:00" },
            { inicio: "17:00:00", fin: "18:30:00" }
        ]
    },
    {
        idPista: 2,
        fecha: new Date().toISOString().slice(0, 10),
        tramosHorariosDisponibles: [
            { inicio: "12:00:00", fin: "13:00:00" },
            { inicio: "19:00:00", fin: "20:30:00" }
        ]
    },
    {
        idPista: 3,
        fecha: new Date().toISOString().slice(0, 10),
        tramosHorariosDisponibles: [
            { inicio: "10:30:00", fin: "12:00:00" },
            { inicio: "18:30:00", fin: "20:00:00" }
        ]
    }
];

let currentProfile = null;

function setFeedback(element, message, type = "") {
    element.textContent = message;
    element.className = "feedback feedback-inline";
    if (type === "success") {
        element.classList.add("is-success");
    }
    if (type === "error") {
        element.classList.add("is-error");
    }
}

function setAvailabilityFeedback(message, type = "") {
    availabilityFeedback.textContent = message;
    availabilityFeedback.className = "feedback";
    if (type === "success") {
        availabilityFeedback.classList.add("is-success");
    }
    if (type === "error") {
        availabilityFeedback.classList.add("is-error");
    }
}

async function parseResponse(response) {
    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json");
    const body = isJson ? await response.json().catch(() => null) : await response.text().catch(() => "");

    if (!response.ok) {
        const looksLikeHtml = typeof body === "string" && /<html|<!DOCTYPE html>/i.test(body);
        if (looksLikeHtml) {
            throw new Error("El backend no está disponible en esta vista previa. Abre la app desde Spring Boot o usa el modo demo.");
        }
        const message =
            (body && typeof body === "object" && (body.message || body.error)) ||
            (typeof body === "string" && body.trim()) ||
            `Error ${response.status}`;
        throw new Error(message);
    }

    return body;
}

function setLoggedOutState() {
    currentProfile = null;
    statusPill.textContent = "Sesión no iniciada";
    sessionHeadline.textContent = "Sesión lista cuando tú lo estés";
    sessionSummary.textContent = "Accede a tu cuenta y recupera tu perfil sin interrupciones ni pasos innecesarios.";
    logoutButton.classList.add("hidden");
    accountPanel.classList.add("hidden");
    tabPanels.forEach((panel) => panel.classList.remove("hidden"));
}

function setLoggedInState(profile) {
    currentProfile = profile;
    statusPill.textContent = `${profile.nombre} · ${profile.rol}`;
    sessionHeadline.textContent = `Hola, ${profile.nombre}`;
    sessionSummary.textContent = "Tu sesión está activa y la experiencia queda lista para continuar sin fricción.";
    profileName.textContent = `${profile.nombre} ${profile.apellidos}`;
    profileEmail.textContent = profile.email;
    profileRole.textContent = profile.rol;
    logoutButton.classList.remove("hidden");
}

function activateTab(targetId) {
    tabButtons.forEach((button) => {
        const isActive = button.dataset.tabTarget === targetId;
        button.classList.toggle("is-active", isActive);
        button.setAttribute("aria-selected", String(isActive));
    });

    tabPanels.forEach((panel) => {
        const isActive = panel.id === targetId;
        panel.classList.toggle("is-active", isActive);
        panel.hidden = !isActive;
    });

    accountPanel.classList.add("hidden");
}

function openAuthModal(initialTab = "loginPanel") {
    authModal.classList.remove("hidden");
    authModal.setAttribute("aria-hidden", "false");

    if (currentProfile) {
        tabPanels.forEach((panel) => panel.classList.add("hidden"));
        accountPanel.classList.remove("hidden");
    } else {
        activateTab(initialTab);
    }
}

function closeAuthModal() {
    authModal.classList.add("hidden");
    authModal.setAttribute("aria-hidden", "true");
}

function formatTime(value) {
    return String(value || "").slice(0, 5);
}

function renderAvailability(items) {
    if (!items.length) {
        availabilityResults.innerHTML = `
            <article class="availability-empty">
                <h3>Sin resultados</h3>
                <p>No hemos encontrado pistas disponibles para la fecha elegida.</p>
            </article>
        `;
        return;
    }

    availabilityResults.innerHTML = items.map((item) => {
        const slots = Array.isArray(item.tramosHorariosDisponibles) ? item.tramosHorariosDisponibles : [];
        const slotMarkup = slots.length
            ? slots.map((slot) => `<span class="slot">${formatTime(slot.inicio)} - ${formatTime(slot.fin)}</span>`).join("")
            : `<span class="slot-empty">Sin huecos disponibles.</span>`;

        return `
            <article class="availability-card">
                <h3>Pista ${item.idPista}</h3>
                <p class="availability-date">${item.fecha}</p>
                <div class="slot-list">${slotMarkup}</div>
            </article>
        `;
    }).join("");
}

async function loadAvailability(event) {
    if (event) {
        event.preventDefault();
    }

    const formData = new FormData(availabilityForm);
    const date = formData.get("date");
    const params = new URLSearchParams({ date: String(date) });

    if (isPreviewMode) {
        const demoItems = previewAvailability.map((item) => ({
            ...item,
            fecha: String(date)
        }));
        renderAvailability(demoItems);
        setAvailabilityFeedback("Vista previa activa: mostrando disponibilidad de demostración.", "success");
        return;
    }

    setAvailabilityFeedback("Actualizando agenda...");

    try {
        const response = await parseResponse(await fetch(`/pistaPadel/availability?${params.toString()}`));
        const items = Array.isArray(response) ? response : [response];
        renderAvailability(items);
        setAvailabilityFeedback(`Agenda actualizada para ${items.length} pista(s).`, "success");
    } catch (error) {
        availabilityResults.innerHTML = `
            <article class="availability-empty">
                <h3>No se ha podido cargar</h3>
                <p>${error.message}</p>
            </article>
        `;
        setAvailabilityFeedback(error.message, "error");
    }
}

async function loadProfile() {
    if (isPreviewMode) {
        setLoggedOutState();
        statusPill.textContent = "Vista previa";
        sessionSummary.textContent = "Estás viendo una previsualización visual. El acceso real funciona al abrir la app desde Spring Boot.";
        return;
    }

    try {
        const profile = await parseResponse(await fetch("/pistaPadel/auth/me", {
            credentials: "same-origin"
        }));
        setLoggedInState(profile);
        if (!authModal.classList.contains("hidden")) {
            openAuthModal();
        }
    } catch (_error) {
        setLoggedOutState();
    }
}

async function submitLogin(event) {
    event.preventDefault();
    if (isPreviewMode) {
        setFeedback(loginFeedback, "En la vista previa no hay backend real. Arranca Spring Boot para probar el acceso.", "error");
        return;
    }
    const formData = new FormData(loginForm);
    setFeedback(loginFeedback, "Accediendo...");

    try {
        await parseResponse(await fetch("/pistaPadel/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "same-origin",
            body: JSON.stringify({
                email: formData.get("email"),
                password: formData.get("password")
            })
        }));

        setFeedback(loginFeedback, "Sesión iniciada correctamente.", "success");
        await loadProfile();
        setTimeout(closeAuthModal, 600);
    } catch (error) {
        setFeedback(loginFeedback, error.message, "error");
    }
}

async function submitRegister(event) {
    event.preventDefault();
    if (isPreviewMode) {
        setFeedback(registerFeedback, "En la vista previa no hay registro real. Arranca Spring Boot para probarlo.", "error");
        return;
    }
    const formData = new FormData(registerForm);
    setFeedback(registerFeedback, "Creando tu cuenta...");

    try {
        const profile = await parseResponse(await fetch("/pistaPadel/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nombre: formData.get("nombre"),
                apellidos: formData.get("apellidos"),
                email: formData.get("email"),
                telefono: formData.get("telefono"),
                password: formData.get("password")
            })
        }));

        setFeedback(registerFeedback, `Cuenta creada para ${profile.nombre}. Ya puedes entrar.`, "success");
        registerForm.reset();
        activateTab("loginPanel");
    } catch (error) {
        setFeedback(registerFeedback, error.message, "error");
    }
}

async function logout() {
    if (isPreviewMode) {
        setLoggedOutState();
        closeAuthModal();
        return;
    }

    try {
        await fetch("/pistaPadel/auth/logout", {
            method: "POST",
            credentials: "same-origin"
        });
    } finally {
        setLoggedOutState();
        closeAuthModal();
    }
}

function setupReveal() {
    if (!("IntersectionObserver" in window)) {
        revealItems.forEach((item) => item.classList.add("is-visible"));
        return;
    }

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.16
    });

    revealItems.forEach((item) => observer.observe(item));
}

authTriggers.forEach((trigger) => {
    trigger.addEventListener("click", () => {
        const target = trigger.dataset.openAuth === "register" ? "registerPanel" : "loginPanel";
        openAuthModal(target);
    });
});

tabButtons.forEach((button) => {
    button.addEventListener("click", () => activateTab(button.dataset.tabTarget));
});

closeModalButton.addEventListener("click", closeAuthModal);
authModal.addEventListener("click", (event) => {
    if (event.target === authModal) {
        closeAuthModal();
    }
});

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !authModal.classList.contains("hidden")) {
        closeAuthModal();
    }
});

availabilityForm.addEventListener("submit", loadAvailability);
loginForm.addEventListener("submit", submitLogin);
registerForm.addEventListener("submit", submitRegister);
logoutButton.addEventListener("click", logout);
logoutModalButton.addEventListener("click", logout);

window.addEventListener("DOMContentLoaded", async () => {
    setupReveal();
    await loadProfile();
    await loadAvailability();
});
