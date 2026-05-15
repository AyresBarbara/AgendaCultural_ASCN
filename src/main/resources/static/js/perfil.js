let token = localStorage.getItem("token");

if (!token) {
    window.location.href = "/login";
}

function showMessage(msg, type = "success") {
    const msgDiv = document.getElementById("message");
    msgDiv.textContent = msg;
    msgDiv.className = "message message-" + type;
    msgDiv.style.display = "block";
    setTimeout(() => {
        msgDiv.style.display = "none";
    }, 3000);
}

function escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

function formatarData(dataStr) {
    if (!dataStr) return "Data não definida";
    const data = new Date(dataStr);
    return data.toLocaleString("pt-BR");
}

// Carregar dados do usuário logado
async function carregarDadosUsuario() {
    try {
        const response = await fetch("/api/users/me", {
            headers: { "Authorization": "Bearer " + token }
        });
        const user = await response.json();
        
        const html = `
            <p><strong>Nome:</strong> ${escapeHtml(user.name)}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Cadastro:</strong> ${formatarData(user.registrationDate)}</p>
        `;
        document.getElementById("dados-usuario").innerHTML = html;
    } catch (error) {
        document.getElementById("dados-usuario").innerHTML = "<p style='color: red;'>Erro ao carregar dados</p>";
    }
}

// Carregar eventos favoritos
async function carregarFavoritos() {
    try {
        const response = await fetch("/api/users/me", {
            headers: { "Authorization": "Bearer " + token }
        });
        const user = await response.json();
        
        const favResponse = await fetch(`/api/favorites/user/${user.idUser}`, {
            headers: { "Authorization": "Bearer " + token }
        });
        const favoritos = await favResponse.json();
        
        if (!favoritos || favoritos.length === 0) {
            document.getElementById("eventos-favoritos").innerHTML = "<p>Nenhum evento favoritado ainda.</p>";
            return;
        }
        
        let html = '<div class="events-grid">';
        for (const fav of favoritos) {
            const eventResponse = await fetch(`/api/events/${fav.eventId}`);
            const evento = await eventResponse.json();
            
            html += `
                <div class="event-card">
                    <div class="event-header">
                        <h3>${escapeHtml(evento.title)}</h3>
                        <div class="event-category">${evento.category?.name || "Sem categoria"}</div>
                    </div>
                    <div class="event-body">
                        <p class="event-description">${escapeHtml(evento.description || "Sem descrição")}</p>
                        <div class="event-info">
                            <span>📍 ${escapeHtml(evento.location || "Local não informado")}</span>
                            <span>📅 ${formatarData(evento.dateTime)}</span>
                        </div>
                    </div>
                </div>
            `;
        }
        html += '</div>';
        document.getElementById("eventos-favoritos").innerHTML = html;
    } catch (error) {
        document.getElementById("eventos-favoritos").innerHTML = "<p style='color: red;'>Erro ao carregar favoritos</p>";
    }
}

// Carregar eventos criados pelo usuário
async function carregarEventosCriados() {
    try {
        const response = await fetch("/api/users/me", {
            headers: { "Authorization": "Bearer " + token }
        });
        const user = await response.json();
        
        const eventsResponse = await fetch("/api/events");
        const todosEventos = await eventsResponse.json();
        
        const meusEventos = todosEventos.filter(e => e.createdById === user.idUser);
        
        if (meusEventos.length === 0) {
            document.getElementById("eventos-criados").innerHTML = "<p>Você ainda não criou nenhum evento.</p>";
            return;
        }
        
        let html = '<div class="events-grid">';
        meusEventos.forEach(evento => {
            html += `
                <div class="event-card">
                    <div class="event-header">
                        <h3>${escapeHtml(evento.title)}</h3>
                        <div class="event-category">${evento.category?.name || "Sem categoria"}</div>
                    </div>
                    <div class="event-body">
                        <p class="event-description">${escapeHtml(evento.description || "Sem descrição")}</p>
                        <div class="event-info">
                            <span>📍 ${escapeHtml(evento.location || "Local não informado")}</span>
                            <span>📅 ${formatarData(evento.dateTime)}</span>
                        </div>
                    </div>
                </div>
            `;
        });
        html += '</div>';
        document.getElementById("eventos-criados").innerHTML = html;
    } catch (error) {
        document.getElementById("eventos-criados").innerHTML = "<p style='color: red;'>Erro ao carregar eventos</p>";
    }
}

// Inicializar
document.addEventListener("DOMContentLoaded", () => {
    carregarDadosUsuario();
    carregarFavoritos();
    carregarEventosCriados();
});