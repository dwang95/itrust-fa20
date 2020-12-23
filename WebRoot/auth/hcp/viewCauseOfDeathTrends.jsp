<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@page import="edu.ncsu.csc.itrust.action.ViewCauseOfDeathTrendsAction"%>

<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - View Cause of Death Reports";
%>

<%@include file="/header.jsp" %>


<%
String[] fields = new String[2];
ViewCauseOfDeathTrendsAction action = new ViewCauseOfDeathTrendsAction(prodDAO, loggedInMID.longValue());
%>
    <div align="center">
        <h2>Cause of Death Reports</h2>
        <span style="font-size: 13pt; font-weight: bold;">Select Options</span>
        <form method="post">
            <table>
                <tr style="text-align: right;">
                    <td>
                        <label for="startYear">Starting Year: </label>
                        <input type="text" name="startYear" id="startYear" value="" />
                    </td>
                    <td style="padding-left: 10px; padding-right: 10px;">
                        <label for="endYear">Ending Year: </label>
                        <input type="text" name="endYear" id="endYear" value="" />
                    </td>
                    <td>
                        <select name="population">
                            <option value="All">All Patients</option>
                            <option value="Male">Male Patients</option>
                            <option value="Female">Female Patients</option>
                        </select>
                    </td>
                </tr>
                
                <tr style="text-align: center;">
                    <td colspan="3">
                        <input type="submit" name="generateReport" value="Generate Report" />
                    </td>
                </tr>
            </table>
        </form>
    </div>
<%
if(request.getParameter("generateReport") != null ) {
    int start = -1;
    int end = -1;
    String startYear = request.getParameter("startYear");
    String endYear = request.getParameter("endYear");
    String population = request.getParameter("population");
    try {
        start = Integer.parseInt(startYear);
        end = Integer.parseInt(endYear);
    }
    catch (NumberFormatException e){}

    if (start < 0 || end < 0 || start>end){%>
        <span class="itrustError" style="color :red; font-weight: bold;">Error: Please Enter Valid Dates</span>
<%  }
    else {
        List<String> yourPatients =  null;
        List<String> allPatients = null;
        yourPatients = action.getMyTopCausesOfDeath(start,end, population);
        allPatients = action.getAllTopCausesOfDeath(start,end, population);
    
%>
    <div>
        Report Generated for <%= population %> Patients from <%= start %> to <%= end %>:
    </div>
    <br>
    
    <table class="fancyTable" align="center">
        <tbody>

            <tr>
                <td>Your Patients:</td>
            </tr>
            <% for (String s : yourPatients){%>
            <tr style="font-weight: bold;" >
                <td>&emsp;<%= s%></td>
            </tr>
            <% } %>


            <tr>
                <td><%= population %> Patients:</td>
            </tr>

            <% for (String s : allPatients){%>
                <tr style="font-weight: bold;" >
                    <td>&emsp;<%= s%></td>
                </tr>
            <% } %>

        </tbody>
    </table>
<%
    }
}
%>

<%@include file="/footer.jsp" %>