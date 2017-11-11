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
        $response['success'] = 0;
        $response['message'] = 'Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }
    
    $emailID    = $_POST['email'];
    $password   = $_POST['password'];
    
    $stmt       = $conn->prepare('SELECT name,stop,college,cemail,email FROM student WHERE email=? AND password=?');
    if(!$stmt)
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }
    $stmt       ->bind_param('ss',$emailID,$password);
    $stmt       ->execute();
    $stmt       ->store_result();
    $stmt       ->bind_result($name,$stop,$college,$cemail,$email); 
    
    if($stmt->num_rows()<=0)
    {
        $response['success'] = 0;
        $response['message'] = 'Wrong Username or Password.';
        echo json_encode($response);
        die();
    }

    $response['success']     = 1;
    
    $student=array();
    while ($stmt->fetch())
    {
        $student['name']     = $name;
        $student['college']  = $college;
        $student['cemail']   = $cemail;
        $student['email']    = $email;
    }
    $response["student"]=$student;
    
    $stmt      = $conn->prepare('SELECT stop,bus1,bus2,bus3 FROM stops WHERE stop=?');
    
    if(!$stmt)
    {
        $response['success']  =0;
        $response['message']  ='Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('s',$stop);
    $stmt       ->execute();
    $stmt       ->store_result();
    $stmt       ->bind_result($stop,$bus1,$bus2,$bus3); 
    $stopArray=array();
    
    while ($stmt->fetch())
    {
      $stopArray['stop']    = $stop;
      $stopArray['bus1']    = $bus1;
      $stopArray['bus2']    = $bus2;
      $stopArray['bus3']    = $bus3;
    }
    $response['stop'] = $stopArray;
    echo json_encode($response);
    
?>
