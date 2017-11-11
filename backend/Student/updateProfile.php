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
        $response['success']=0;
        $response['message']='Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }
    
    $name       =$_POST['name'];
    $stop       =$_POST['stop'];
    $college    =$_POST['college'];
    $cEmailID   =$_POST['cEmailID'];
    $emailID    =$_POST['emailID'];

    $cemail    = $cEmailID;

    
    $sql        ="SELECT * FROM student WHERE email='".$emailID."'";
    $result     = mysqli_query($conn, $sql);
    
    if(!$result)
    {
      $response['success']  =0;
      $response['message']  ='Oops! An error occurred.';
      echo json_encode($response);
      die();
    }
    if(mysqli_num_rows($result)<=0)
    {
      $response['success']  =0;
      $response['message']  ='Wrong Email Address.';
      echo json_encode($response);
      die();
    }
     
    $stmt      =$conn->prepare('UPDATE student set name=?,stop=?,college=?,cemail=?,email=? WHERE email=?');

    if(!$stmt)
    {
        $response['success']=-0;
        $response['message']='47 Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('ssssss',$name,$stop,$college,$cEmailID,$emailID,$emailID);
    $stmt       ->execute();
    if($stmt)
    {
        $stmt       =$conn->prepare('SELECT name,stop,college,cemail,email FROM student WHERE email=?');
        if(!$stmt)
        {
            $response['success'] = 0;
            $response['message'] = '62 Oops! An error occurred.';
            echo json_encode($response);
            die();
        }
        $stmt       ->bind_param('s',$emailID);
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
            $student['stop']     = $stop;
            $student['cemail']   = $cemail;
            $student['email']    = $email;
        }
    
        $cemail    = str_replace("@","_",$cemail);
        $cemail    = str_replace(".","_",$cemail);
        $stmt      = $conn->prepare('SELECT stop,bus1,bus2,bus3 FROM '.$cemail.'_stops WHERE stop=?');
    
        if(!$stmt)
        {
            $response['success']  =0;
            $response['message']  ='96 Oops! An error occurred.';
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
        $response['success']=1;
        $response['message']='Successfully updated.';
        $response['stop'] = $stopArray;
        $response["student"]=$student;

        echo json_encode($response);
    }
    else
    {
        $response['success']=-0;
        $response['message']='Oops! An error occurred.';
        echo json_encode($response);
    }    
?>
