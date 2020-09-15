<%--
  Created by IntelliJ IDEA.
  User: ViCesar18
  Date: 13/09/2020
  Time: 23:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="model.Usuario" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <%@include file="../include/head.jsp"%>
        <title>Rede Musical: Perfil</title>
    </head>
    <body>
        <%
            Usuario user = (Usuario)request.getAttribute("usuario");
            String imgName = user.getImagem();

            if(imgName == null || imgName.isBlank()) {
                imgName = "default_avatar.png";
            }

            request.setAttribute("imgName", imgName);

            Date dt = user.getDtNascimento();
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(dt.getTime()));

            request.setAttribute("date", date);
        %>
        <div class="container">
            <div class="text-center">
                <img
                    src="${pageContext.request.contextPath}/assets/img/usuario/${imgName}"
                    class="rounded-circle"
                    alt="Avatar"
                    height="350"
                    width="350"
                >

                <h1>${usuario.getpNome()} ${usuario.getsNome()}</h1>
            </div>

            <div>
                <p><strong>Email:</strong> ${usuario.getEmail()}</p>
                <p><strong>Data de Nascimento:</strong> ${date}</p>
                <p><strong>Email:</strong> ${usuario.getEmail()}</p>
                <p><strong>Local:</strong> ${usuario.getCidade()}/${usuario.getEstado()}/${usuario.getPais()}</p>
                <p><strong>Banda Favorita:</strong> ${usuario.getBandaFavorita()}</p>
                <p><strong>Música Favorita:</strong> ${usuario.getMusicaFavorita()}</p>
                <p><strong>Gênero Favorito:</strong> ${usuario.getGeneroFavorito()}</p>
                <p><strong>Instrumento Favorito:</strong> ${usuario.getInstrumentoFavorito()}</p>
            </div>

            <div>
                <a type="button" class="btn btn-primary" href="${pageContext.servletContext.contextPath}/usuario/perfil/update">Editar Perfil</a>

                <a type="button" class="btn btn-success" href="${pageContext.servletContext.contextPath}/usuario/perfil/update-musical">Editar Perfil Musical</a>

                <a type="button" class="btn btn-warning" href="${pageContext.servletContext.contextPath}/usuario/perfil/update-foto">Alterar Foto de Perfil</a>

                <a type="button" class="btn btn-warning" href="${pageContext.servletContext.contextPath}/usuario/perfil/update-senha">Alterar Senha</a>

                <a type="button" class="btn btn-danger" href="${pageContext.servletContext.contextPath}/usuario/perfil/delete">Deletar Usuário</a>

                <a type="button" class="btn btn-danger" href="${pageContext.servletContext.contextPath}/feed">Voltar</a>
            </div>
        </div>

        <%@include file="../include/scripts.jsp"%>
    </body>
</html>
