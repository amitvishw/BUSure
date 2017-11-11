<?php
    //error_reporting(0);
    $response   = array();
    $servername = 'localhost';
    $username   = 'root';
    $password   = '';
    $dbname     = 'busure';
    $conn       = new mysqli($servername, $username, $password,$dbname);

    if($conn->connect_error)
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! Can not connect to database';
        echo json_encode($response);
        die();
    }


    $emailID    = $_POST['emailID'];
    $password   = $_POST['password'];
    $status     = 'Y';

    $stmt       = $conn->prepare('SELECT * FROM college WHERE email=? AND password=?');
    if(!$stmt)
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('ss',$emailID,$password);
    $stmt       ->execute();
    $stmt       ->store_result();
    $stmt       ->bind_result($name,$city,$state,$pincode,$phone,$emailID,$password,$OTP,$confirm); 
    if($stmt->num_rows()>0)
    {
        while ($stmt->fetch())
        {
            if($confirm==$status)
            {
                $response['success']  = 1;
                $response['name']     = $name;
                $response['city']     = $city;
                $response['state']    = $state;
                $response['pincode']  = $pincode;
                $response['phone']    = $phone;
                $response['emailID']  = $emailID;
                $response['password'] = $password;
                echo json_encode($response);
            }
            else
            {
                $response['success']  = 2;
                $response['message']  = 'Your Email Is Not Confirmed.';
                $response['confirm']  = $confirm; 
                echo json_encode($response);
                die();
            }
        }
    }
    else
    {
        $response['success']    = 0;
        $response['message']    = 'Wrong Username or Password.';
        echo json_encode($response);
    }
?>