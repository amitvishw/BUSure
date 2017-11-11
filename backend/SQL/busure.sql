CREATE TABLE IF NOT EXISTS `college` (
  `name` varchar(100) NOT NULL,
  `city` varchar(30) NOT NULL,
  `state` varchar(30) NOT NULL,
  `pincode` int(11) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `email` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `OTP` int(11) NOT NULL DEFAULT '-1',
  `confirm` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`email`)
)

CREATE TABLE IF NOT EXISTS `stops` (
  `collegeid` varchar(30) NOT NULL,
  `stop` varchar(50) NOT NULL,
  `bus1` int(11) DEFAULT NULL,
  `bus2` int(11) DEFAULT NULL,
  `bus3` int(11) DEFAULT NULL,
  KEY `collegeid` (`collegeid`)
)

CREATE TABLE IF NOT EXISTS `student` (
  `name` varchar(100) NOT NULL,
  `stop` varchar(50) NOT NULL,
  `college` varchar(150) NOT NULL,
  `cemail` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `stops`
  ADD CONSTRAINT `stops_ibfk_1` FOREIGN KEY (`collegeid`) REFERENCES `college` (`email`) ON DELETE CASCADE;
