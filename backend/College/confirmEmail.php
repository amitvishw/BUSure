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
        $response['message']    = 'Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }

    $emailID    = $_POST['emailID'];
    $code       = $_POST['code'];
    $confirm    = 'Y';
    
    $stmt       = $conn->prepare('SELECT OTP FROM college WHERE email=?');
    if(!$stmt)
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! An error occurred.25';
        echo json_encode($response);
        die();
    }
    $stmt       ->bind_param('s',$emailID);
    $stmt       ->execute();
    $stmt       ->store_result();
    $stmt       ->bind_result($OTP); 
     if($stmt->num_rows()>0)
    {
        while ($stmt->fetch())
        {
            if($code==$OTP)
            {
                $stmt2       = $conn->prepare('UPDATE college SET confirm=? where email=?');
                if(!$stmt2)
                {
                    $response['success']    = 0;
                    $response['message']    = 'Oops! An error occurred. 45';
                    echo json_encode($response);
                    die();
                }
                $stmt2       ->bind_param('ss',$confirm,$emailID);
                $stmt2       ->execute();
                if($stmt)
                {
                    $response['success']    = 1;
                    $response['message']    = 'Email Confirmed';
                    echo json_encode($response);
                }
                else
                {
                    $response['success']    = 0;
                    $response['message']    = 'Oops! An error occurred.58';
                    echo json_encode($response);
                }
            }
            else
            {
                $response['success']    = 0;
                $response['message']    = 'Wrong Code.57';
                echo json_encode($response);
                die();
            }
        }
    }
    else
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! An error occurred';
        echo json_encode($response);
    }
?>
