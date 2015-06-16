<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8" import="edu.neu.ccis.sms.constants.SessionKeys,javax.servlet.http.HttpSession,java.util.*,javax.servlet.http.HttpServletRequest,edu.neu.ccis.sms.dao.users.UserDao,edu.neu.ccis.sms.dao.users.UserDaoImpl,edu.neu.ccis.sms.entity.users.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    /* Load all current Topics of interest */
    Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);
    System.out.println("Session userId - "+userId);
    
    // Get the UserDaoImpl instance
    UserDao userDao = new UserDaoImpl();
    User one = userDao.getUser(userId);

    Set<User> coiSet = one.getMyConflictsOfInterestWithUsers();

    System.out.println("Total Number of coiSet - "+coiSet.size());
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Update User's Conflicts of Interest</title>
        <style>
            body {
              font-family: 'Roboto', sans-serif;
            }
            .form_header {
              font-weight: 100;
              text-align: left;
              font-size: 1.8em;
            }
        </style>

        <script type="text/javascript">
            var i = 1;
            function addField(tableid)
            {
               var row = document.createElement("tr");
               var col1 = document.createElement("td");
               var col2 = document.createElement("td");
               var input = document.createElement("input");

               input.setAttribute("type","text");
               input.setAttribute("name","coifield" + i++ );
               input.setAttribute("value","");
               input.setAttribute("size","50");
               input.setAttribute("maxlength","254");
               input.setAttribute("placeholder","Enter user email-id");

               var deleteButton = document.createElement("input");
               deleteButton.setAttribute("type","button");
               deleteButton.setAttribute("style","width:150px;");
               deleteButton.setAttribute("value","(-) Delete Field");
               deleteButton.setAttribute("title","Delete this field");
               deleteButton.setAttribute("onclick","removeRow(this)");
               
               col1.appendChild(input);
               col2.appendChild(deleteButton);
               row.appendChild(col1);
               row.appendChild(col2);
               var table = document.getElementById(tableid);
               table.appendChild(row);
            }
            
            function removeRow(x)
            {
                var table = x.parentNode.parentNode.parentNode;
                var rowIndex = x.parentNode.parentNode.rowIndex;
                table.deleteRow(rowIndex);
            }
            
            function resetForm(tableid)
            {
               var table = document.getElementById(tableid);
               var rowsLength = table.rows.length;
               for(var i = rowsLength-1; i>0; i--){
                   table.deleteRow(i);
               }
            }
        </script>
    </head>
    <body>
        <div class="form_header">Update Conflicts of Interest</div>
        <hr/>
        <% 
            StringBuffer coiEmailList = new StringBuffer();
            for (User coi : coiSet) {
                coiEmailList.append(coi.getEmail()).append(", ");
            }
            out.println("<textarea rows='4' placeholder='No conflicts of interest found.' cols='100' disabled readonly>" + 
                        coiEmailList + "</textarea>");
        %>
        <br/>
        <br/>
        <form action="<%=request.getContextPath()%>/UpdateCOIForUser" method="POST">
            <label style="width:400px;display:inline-block;">Enter Conflicts of Interest</label><br/>
            <font size="2">(Note:Invalid user email ids will be skipped)</font><br/><br/>
            <table id="fieldTable">
                <tr>
                    <td><input type="text" placeholder="Enter user email-id" name="coifield0" value="" size=50 maxlength=254/></td>
                </tr>
            </table>
            <br/>
            <table>
                <tr>
                    <td>
                    <input style="width:150px;" title="Add conflicts field" type="button" value="(+) Add Field" id="button"  onclick="addField('fieldTable')"/>
                    </td>
                    <td><input style="width:150px;" title="Reset form" type="button" value="Reset Form" id="button"  onclick="resetForm('fieldTable')" /></td>
                </tr>
            </table>
            <table>
                <tr>
                    <td><input style="width:150px;" title="Add new conflicts list to previous ones" type="submit" name="submitType" value="Add Conflicts"/></td>
                    <td><input style="width:150px;" title="Replace old conflicts with new ones" type="submit" name="submitType" value="Replace Conflicts"/></td>
                    <td><input style="width:150px;" title="Removes all conflicts of interest" type="submit" name="submitType" value="Clear All Conflicts"/></td>
                </tr>
            </table>
        </form>
        <br/>
        <hr/>
    </body>
</html>

