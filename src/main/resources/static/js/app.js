let token = localStorage.getItem("token");
let eventoAtualId = null;

function showMessage(msg, type = "success") {
    const msgDiv = document.getElementById("message");
    msgDiv.textContent = msg;
    msgDiv.className = "message message-" + type;
    msgDiv.style.display = "block";
    setTimeout(() => {
        msgDiv.style.display = "none";
    }, 3000);
}

function abrirModal(id) {
    document.getElementById(id).style.display = "flex";
}

function fecharModal(id) {
    document.getElementById(id).style.display = "none";
}

function abrirModalCriarEvento() {
    abrirModal("modal-criar-evento");
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
}

function formatarData(dataStr) {
    if (!dataStr) return "Data não definida";
    const data = new Date(dataStr);
    return data.toLocaleString("pt-BR");
}

function escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

async function carregarEventos() {
    const container = document.getElementById("events-grid");
    
    try {
        const response = await fetch("/api/events");
        const eventos = await response.json();
        
        if (!eventos || eventos.length === 0) {
            container.innerHTML = '<div class="loading-container">Nenhum evento encontrado.</div>';
            return;
        }
        
        // Busca o ID do usuário logado
        const userId = await getUsuarioId();
        
        // Busca os favoritos do usuário logado (se estiver logado)
        let favoritosIds = [];
        if (token && userId) {
            try {
                const favResponse = await fetch(`/api/favorites/user/${userId}`, {
                    headers: { "Authorization": "Bearer " + token }
                });
                const favoritos = await favResponse.json();
                favoritosIds = favoritos.map(f => f.eventId);
            } catch (e) {
                console.log("Erro ao buscar favoritos:", e);
            }
        }
        
        let html = "";
        eventos.forEach(evento => {
            // Verifica se o evento está favoritado
            const isFavoritado = favoritosIds.includes(evento.idEvent);
            const textoBotaoFavorito = isFavoritado ? "💔 Desfavoritar" : "❤️ Favoritar";
            
            // Só mostra botão excluir se o usuário logado for o criador
            const botoesExcluir = (userId && evento.createdById === userId) 
                ? `<button class="btn-delete" onclick="deletarEvento('${evento.idEvent}')">🗑️ Excluir</button>` 
                : '';
            
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
                        <div class="event-actions">
                            <button class="btn-favorite" onclick="favoritarEvento('${evento.idEvent}', this)">${textoBotaoFavorito}</button>
                            ${botoesExcluir}
                            <button class="btn-comment" onclick="abrirComentarios('${evento.idEvent}')">💬 Comentar</button>
                        </div>
                    </div>
                </div>
            `;
        });
        container.innerHTML = html;
    } catch (error) {
        console.error("Erro:", error);
        container.innerHTML = '<div class="loading-container" style="color: red;">Erro ao carregar eventos</div>';
    }
}

async function alternarFavorito(eventoId, botao) {
    if (!token) {
        showMessage("Faça login para favoritar", "error");
        return;
    }
    
    const textoBotao = botao.textContent;
    const isFavoritando = textoBotao.includes("Favoritar");
    
    const userId = await getUsuarioId();
    if (!userId) return;
    
    const url = isFavoritando ? "/api/favorites" : `/api/favorites/user/${userId}/event/${eventoId}`;
    const method = isFavoritando ? "POST" : "DELETE";
    const body = isFavoritando ? JSON.stringify({ eventId: eventoId }) : null;
    
    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: body
        });
        
        if (response.ok) {
            if (isFavoritando) {
                botao.textContent = "💔 Desfavoritar";
                showMessage("❤️ Adicionado aos favoritos!");
            } else {
                botao.textContent = "❤️ Favoritar";
                showMessage("💔 Removido dos favoritos!");
            }
        } else {
            showMessage("❌ Erro ao " + (isFavoritando ? "favoritar" : "desfavoritar"), "error");
        }
    } catch (error) {
        showMessage("❌ Erro: " + error.message, "error");
    }
}

async function carregarCategorias() {
    try {
        const response = await fetch("/api/categories");
        const categorias = await response.json();
        const select = document.getElementById("evento-categoria");
        select.innerHTML = '<option value="">Selecione uma categoria</option>';
        categorias.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat.idCategory;
            option.textContent = cat.name;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Erro ao carregar categorias:", error);
    }
}

async function criarEvento(event) {
    event.preventDefault();
    
    const userId = await getUsuarioId();
    if (!userId) {
        showMessage("Erro ao identificar usuário. Faça login novamente.", "error");
        return;
    }
    
    const title = document.getElementById("evento-titulo").value;
    const description = document.getElementById("evento-descricao").value;
    const location = document.getElementById("evento-local").value;
    const dateTime = document.getElementById("evento-data").value;
    const categoryId = document.getElementById("evento-categoria").value;
    
    if (!title || !location || !dateTime || !categoryId) {
        showMessage("Preencha todos os campos (incluindo categoria)", "error");
        return;
    }
    
    const btn = event.submitter;
    const originalText = btn.textContent;
    btn.textContent = "Criando...";
    btn.disabled = true;
    
    const evento = {
        title: title,
        description: description,
        location: location,
        dateTime: dateTime,
        categoryId: categoryId,
        createdById: userId   
    };
    
    console.log("Enviando evento:", evento);
    
    try {
        const response = await fetch("/api/events", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(evento)
        });
        
        const text = await response.text();
        console.log("Resposta:", text);
        
        if (response.ok) {
            showMessage("✅ Evento criado com sucesso!");
            fecharModal("modal-criar-evento");
            document.getElementById("form-criar-evento").reset();
            carregarEventos();
        } else {
            showMessage("❌ Erro: " + text, "error");
        }
    } catch (error) {
        console.log("Erro:", error);
        showMessage("❌ Erro ao criar evento: " + error.message, "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

// Pega o ID do usuário logado
async function getUsuarioId() {
    try {
        const response = await fetch("/api/users/me", {
            headers: { "Authorization": "Bearer " + token }
        });
        const user = await response.json();
        return user.idUser;
    } catch (error) {
        console.error("Erro ao buscar ID do usuário:", error);
        return null;
    }
}

async function favoritarEvento(eventoId, botao) {
    if (!token) {
        showMessage("Faça login para favoritar", "error");
        return;
    }
    
    const textoBotao = botao.textContent;
    const isFavoritando = textoBotao.includes("Favoritar");
    const userId = await getUsuarioId();
    
    const url = isFavoritando ? "/api/favorites" : `/api/favorites/user/${userId}/event/${eventoId}`;
    const method = isFavoritando ? "POST" : "DELETE";
    const body = isFavoritando ? JSON.stringify({ eventId: eventoId }) : null;
    
    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: body
        });
        
        if (response.ok) {
            if (isFavoritando) {
                botao.textContent = "💔 Desfavoritar";
                showMessage("❤️ Adicionado aos favoritos!");
            } else {
                botao.textContent = "❤️ Favoritar";
                showMessage("💔 Removido dos favoritos!");
            }
        } else {
            showMessage("❌ Erro", "error");
        }
    } catch (error) {
        showMessage("❌ Erro: " + error.message, "error");
    }
}

async function desfavoritarEvento(eventoId) {
    if (!token) {
        showMessage("Faça login para desfavoritar", "error");
        return;
    }
    
    const userId = await getUsuarioId();
    if (!userId) return;
    
    try {
        const response = await fetch(`/api/favorites/user/${userId}/event/${eventoId}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        
        if (response.ok) {
            showMessage("💔 Evento removido dos favoritos!");
            carregarEventos();
        } else {
            showMessage("❌ Erro ao desfavoritar", "error");
        }
    } catch (error) {
        showMessage("❌ Erro: " + error.message, "error");
    }
}

async function deletarEvento(eventoId) {
    if (!confirm("Tem certeza que deseja excluir este evento?")) return;
    
    try {
        const response = await fetch(`/api/events/${eventoId}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        
        if (response.ok) {
            showMessage("✅ Evento excluído com sucesso!");
            carregarEventos(); // recarrega a lista
        } else {
            showMessage("❌ Erro ao excluir evento", "error");
        }
    } catch (error) {
        showMessage("❌ Erro: " + error.message, "error");
    }
}

async function abrirComentarios(eventoId) {
    eventoAtualId = eventoId;
    await carregarComentarios(eventoId);
    abrirModal("modal-comentarios");
}

async function carregarComentarios(eventoId) {
    const container = document.getElementById("comentarios-lista");
    container.innerHTML = '<div class="loading"></div> Carregando comentários...';
    
    try {
        const response = await fetch(`/api/comments/event/${eventoId}`);
        const comentarios = await response.json();
        
        if (!comentarios || comentarios.length === 0) {
            container.innerHTML = "<p>Nenhum comentário ainda. Seja o primeiro a comentar!</p>";
            return;
        }
        
        let html = "";
        comentarios.forEach(com => {
            html += `
                <div class="comment-item">
                    <div class="comment-text">${escapeHtml(com.text)}</div>
                    <div class="comment-date">${formatarData(com.date)}</div>
                </div>
            `;
        });
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<p style="color: red;">Erro ao carregar comentários</p>';
    }
}

async function adicionarComentario() {
    const texto = document.getElementById("novo-comentario").value;
    if (!texto.trim()) {
        showMessage("Digite um comentário", "error");
        return;
    }
    
    if (!token) {
        showMessage("Faça login para comentar", "error");
        return;
    }
    
    // Pega o ID do usuário logado
    const userId = await getUsuarioId();
    if (!userId) {
        showMessage("Erro ao identificar usuário", "error");
        return;
    }
    
    const comentario = {
        text: texto,
        eventId: eventoAtualId,
        userId: userId   
    };
    
    try {
        const response = await fetch("/api/comments", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(comentario)
        });
        
        if (response.ok) {
            showMessage("✅ Comentário adicionado!");
            document.getElementById("novo-comentario").value = "";
            await carregarComentarios(eventoAtualId);
        } else {
            const error = await response.json();
            showMessage("❌ " + (error.message || "Erro ao adicionar comentário"), "error");
        }
    } catch (error) {
        showMessage("❌ Erro: " + error.message, "error");
    }
}

// ============================================================
// ADMIN (Backup/Restore)
// ============================================================

async function fazerBackup() {
    const btn = event.target;
    const originalText = btn.textContent;
    btn.textContent = "Gerando...";
    btn.disabled = true;
    
    try {
        const response = await fetch("/api/admin/backup", {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });
        const data = await response.json();
        showMessage("✅ Backup: " + data.arquivo);
        document.getElementById("resultado-backup").innerHTML = "<strong>Backup:</strong><br>" + JSON.stringify(data, null, 2);
    } catch (error) {
        showMessage("❌ Erro ao fazer backup", "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

async function listarBackups() {
    const btn = event.target;
    const originalText = btn.textContent;
    btn.textContent = "Carregando...";
    btn.disabled = true;
    
    try {
        const response = await fetch("/api/admin/backups", {
            headers: { "Authorization": "Bearer " + token }
        });
        const backups = await response.json();
        
        const select = document.getElementById("selectBackup");
        select.innerHTML = '<option value="">Selecione um backup</option>';
        
        backups.forEach(backup => {
            const option = document.createElement("option");
            option.value = backup.nome;
            option.textContent = backup.nome + " (" + backup.tamanho + ")";
            select.appendChild(option);
        });
        
        showMessage(`📁 ${backups.length} backup(s) encontrado(s)`);
    } catch (error) {
        showMessage("❌ Erro ao listar backups", "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

async function restaurarBackup() {
    const nome = document.getElementById("selectBackup").value;
    if (!nome) {
        showMessage("Selecione um backup", "error");
        return;
    }
    
    const btn = event.target;
    const originalText = btn.textContent;
    btn.textContent = "Restaurando...";
    btn.disabled = true;
    
    try {
        const response = await fetch("/api/admin/restore", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ arquivo: nome })
        });
        const data = await response.json();
        showMessage("✅ Restauração concluída!");
        document.getElementById("resultado-backup").innerHTML = "<strong>Restauração:</strong><br>" + JSON.stringify(data, null, 2);


    } catch (error) {
        showMessage("❌ Erro ao restaurar", "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

async function restaurarUpload() {
    const fileInput = document.getElementById("fileUpload");
    const file = fileInput.files[0];
    if (!file) {
        showMessage("Selecione um arquivo .sql", "error");
        return;
    }
    
    const btn = event.target;
    const originalText = btn.textContent;
    btn.textContent = "Enviando...";
    btn.disabled = true;
    
    const formData = new FormData();
    formData.append("file", file);
    
    try {
        const response = await fetch("/api/admin/restore/upload", {
            method: "POST",
            headers: { "Authorization": "Bearer " + token },
            body: formData
        });
        const data = await response.json();
        showMessage("✅ Restauração via upload concluída!");
        document.getElementById("resultado-backup").innerHTML = "<strong>Upload:</strong><br>" + JSON.stringify(data, null, 2);


    } catch (error) {
        showMessage("❌ Erro no upload", "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
        fileInput.value = "";
    }
}

async function listarUsuarios() {
    const btn = event.target;
    const originalText = btn.textContent;
    btn.textContent = "Carregando...";
    btn.disabled = true;
    
    try {
        const response = await fetch("/api/users", {
            headers: { "Authorization": "Bearer " + token }
        });
        const users = await response.json();
        
        let html = `
            <table border="1" cellpadding="8" cellspacing="0" style="border-collapse: collapse; width: 100%;">
                <thead>
                    <tr style="background-color: #f2f2f2;">
                        <th>ID</th>
                        <th>Nome</th>
                        <th>Email</th>
                        <th>Data Cadastro</th>
                    </tr>
                </thead>
                <tbody>
        `;
        
        users.forEach(user => {
            html += `
                <tr>
                    <td>${user.idUser.substring(0, 8)}...</td>
                    <td>${escapeHtml(user.name)}</td>
                    <td>${user.email}</td>
                    <td>${formatarData(user.registrationDate)}</td>
                </tr>
            `;
        });
        
        html += `
                </tbody>
            </table>
        `;
        
        document.getElementById("listaUsuarios").innerHTML = html;
        showMessage(`👥 ${users.length} usuário(s) encontrado(s)`);
    } catch (error) {
        showMessage("❌ Erro ao listar usuários", "error");
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

// ============================================================
// INICIALIZAÇÃO
// ============================================================

if (!token && window.location.pathname !== "/login" && window.location.pathname !== "/register") {
    window.location.href = "/login";
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-criar-evento");
    if (form) {
        form.addEventListener("submit", criarEvento);
        carregarCategorias();
    }
    
    if (document.getElementById("events-grid")) {
        carregarEventos();
    }
});