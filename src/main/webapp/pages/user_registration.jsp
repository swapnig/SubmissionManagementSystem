<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Register New User</title>
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
</head>
<body>
        <div class="form_header">User Registration Form</div>
        <font size=1> Fill in all required details (<sup>*</sup> Required Fields)</font><br/>
        <hr/>
        <form action="<%=request.getContextPath()%>/RegisterUser" method=post>

            <label style="width:150px;display:inline-block;" for="fname">First Name<sup>*</sup></label>
            <input type="text" id="fname" name="firstName" value="" size=30 maxlength=254>
            
            <br/>
            <br/>
            <label style="width:150px;display:inline-block;" for="lname">Last Name<sup>*</sup></label>
            <input type="text" id="lname" name="lastName" value="" size=30 maxlength=254>
            
            <br/>
            <br/>
            <label style="width:150px;display:inline-block;" for="email">E-Mail id<sup>*</sup></label>
            <input type="text" id="email" name="email" value="" size=30 maxlength=254> 
            
            <br/>
            <br/>
            <label style="width:150px;display:inline-block;" for="uname">User Name<sup>*</sup></label>
            <input type="text" id="uname" name="userName" size=30 value="" maxlength=254>         
            
            <br/>
            <br/>
            <label style="width:150px;display:inline-block;" for="pword">Password<sup>*</sup></label> 
            <input type="password" id="pword" name="password" size=30 value="" maxlength=254>
            
            <br/>
            <br/>
            <label style="width:150px;display:inline-block;" for="cpword">Confirm Password<sup>*</sup></label>
            <input type="password" id="cpword" size=30 value="" maxlength=254>

            <br/>
            <br/>
            <span style="align:center;"> 
                <input style="width:150px;" type="submit" value="Submit">
                <input style="width:150px;" type="reset" value="Reset">
            </span>
        </form>
        <hr/>
    </div>
</body>
</html>
