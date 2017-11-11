<?php
	$headers  = "MIME-Version: 1.0" . "\r\n";
	$headers .= "Content-type:text/html;charset=UTF-8" . "\r\n";
	$headers .= 'From: CBus <noreply@bit2hex.xyz>' . "\r\n";
	$message  = 'Thank you for signing up with us. To activate your account enter following code **** ';
	mail('amit.excellence@gmail.com','CBus Email Confirmation',$message,$headers) or die('no mail()');
	echo 'mail sent.';
?>
