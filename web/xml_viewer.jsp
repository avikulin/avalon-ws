<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="master/header.jsp"%>

<div class="card">
  <div class="card-body">
    <form action="data" method="get">

      <h2>Вывод данных их XML-файла</h2>

      <div class="alert alert-danger" role="alert"
              <%    List<String> err = (List<String>) request.getAttribute("LIST_ERRORS");
                    if (err == null || err.isEmpty()) {%>
              style="display: none"
              <%}%> >
        <p>В ходе выполнения операции возникли ошибки:</p>
        <c:forEach items="${LIST_ERRORS}" var="errMsg" varStatus="counter">
          <p>${counter.count}) ${errMsg}.</p>
        </c:forEach>
      </div>

      <ul class="nav nav-tabs" id="mainTabs" role="tablist">
        <li class="nav-item" role="location">
          <button class="nav-link active" id="location-tab-tag" data-bs-toggle="tab" data-bs-target="#location-tab" type="button" role="tab" aria-controls="location-tab" aria-selected="true">Результаты запроса</button>
        </li>
        <li class="nav-item" role="equipment">
          <button class="nav-link" id="equipment-tab-tag" data-bs-toggle="tab" data-bs-target="#equipment-tab" type="button" role="tab" aria-controls="equipment-tab" aria-selected="false">Исходные данные</button>
        </li>
      </ul>

      <div class="tab-content" id="myTabContent">
        <div class="tab-pane fade show active" id="location-tab" role="tabpanel" aria-labelledby="location">
          <div class="form-group">
            <label for="TEXT_XML_QUERY">Параметры отбора записей</label>
            <textarea type="text" rows ="3" maxlength="200" class="form-control" id="TEXT_XML_QUERY"  name="TEXT_XML_QUERY" aria-describedby="xmlQueryHelp" placeholder="Выражение фильтра">${TEXT_XML_QUERY}</textarea>
            <small id="xmlQueryHelp" class="form-text text-muted">Введите выражение фильтрации записей (если необходимо).</small>
          </div>

          <div class="d-flex">
            <div class="p-2">
              <button type="submit" class="btn btn-primary">Отправить запрос</button>
            </div>
          </div>
          <p/>
          <table id="equipmentUnitsTable" class="table table-hover" style="width:100%">
            <thead>
            <tr>
              <th>Код ЕО</th>
              <th>Производитель</th>
              <th>Страна</th>
              <th>Модель</th>
              <th>Уровень OSI</th>
              <th>IP-адрес</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${TABLE_EQUIPMENT_UNITS}" var="eq_unit">
              <tr>
                <td>${eq_unit.getId()}</td>
                <td>${eq_unit.getModel().getVendor().getName()}</td>
                <td>${eq_unit.getModel().getVendor().getCountryOfOrigin().getFullName()}</td>
                <td>${eq_unit.getModel().getModelDescription()}</td>
                <td>${eq_unit.getModel().getLayerNum()}</td>
                <td>${eq_unit.getIpAddress()}</td>
              </tr>
            </c:forEach>
            </tbody>
          </table>

        <script type="text/javascript">
          $(document).ready(function () {
            var table = $('#equipmentUnitsTable').DataTable({
              searching: false,
              language: {
                processing:    "Идет обработка...",
                search:        "Искать:",
                lengthMenu:    "Отображать по _MENU_ записей",
                info:          "Показано с _START_ по _END_ из _TOTAL_ записей",
                infoEmpty:     "Показано с _START_ по _END_ из _TOTAL_ записей",
                infoFiltered:  "(отфильтровано из _MAX_ записей)",
                infoPostFix:   "",
                loadingRecords:"Идет загрузка...",
                zeroRecords:   "Нет данных для отображения",
                emptyTable:    "Нет данных для отображения",
                paginate: {
                  first:     " ◀◀ ",
                  previous:  " ◀ ",
                  next:      " ▶ ",
                  last:      " ▶▶ "
                },
                aria: {
                  sortAscending:  ": Сортировать по возрастанию",
                  sortDescending: ": Сортировать по убыванию"
                }
              }
            });
          });
        </script>
        </div>
        <div class="tab-pane fade" id="equipment-tab" role="tabpanel" aria-labelledby=equipment-tab-tag">
          <pre class="prettyprint">
            <c:out value="${TEXT_XML_CONTENT}"></c:out>
          </pre>
          <script src="https://cdn.jsdelivr.net/gh/google/code-prettify@master/loader/run_prettify.js"></script>
        </div>
      </div>
    </form>
  </div>
</div>
<%@include file="master/footer.jsp"%>