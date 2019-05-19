<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>Nowonbun blog editor</title>
<link rel="stylesheet" href="./css/login.css">
<!--meta name="google-signin-client_id" content="220458850151-v238vlakgd2c4n3dho9o76sc7u9a5ovb.apps.googleusercontent.com"-->
<meta name="google-signin-client_id" content="220458850151-v238vlakgd2c4n3dho9o76sc7u9a5ovb.apps.googleusercontent.com">
</head>
<body>
	<div class="container">
		<form method="POST" action="login.html">
			<input type="hidden" name="id" id="pId" /> 
			<input type="hidden" name="email" id="pEmail" /> 
			<input type="hidden" name="name" id="pName" />
		</form>
		<div class="form-signin">
			<div class="login-box">
				<h3 class="form-signin-heading title">Nowonbun Blog Editor</h3>
				<input type="image" id="signButton" src="./img/login.png">
				<div id="googleSigninButton" style="display:none;"></div>
			</div>
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
	<script type="text/javascript" src="https://apis.google.com/js/platform.js?onload=onLoad" async defer></script>
	<script>
		function onSignIn(googleUser) {
			var profile = googleUser.getBasicProfile();
			$("#pId").val(profile.getId());
			$("#pEmail").val(profile.getEmail());
			$("#pName").val(profile.getName());
			$("form").submit();
		}
		function onLoad() {
			$("#signButton").on("click", function(){
				$(".abcRioButtonContents").trigger("click");
			});
			gapi.load('auth2,signin2', function() {
				var auth2 = gapi.auth2.init();
				auth2.then(function() {
					var isSignedIn = auth2.isSignedIn.get();
					var currentUser = auth2.currentUser.get();
					gapi.signin2.render('googleSigninButton', {
						'onsuccess' : 'onSignIn',
						'longtitle' : true,
						'theme' : 'dark',
						'width' : '0'
					});
				});
			});
		}
	</script>
</body>
</html>