GRANT ALL ON *.* to root@'%' IDENTIFIED BY 'sesame';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS glass_room;
USE glass_room;

CREATE TABLE IF NOT EXISTS `settings` (
  `id` int(11) NOT NULL,
  `bed_height` int(11) NOT NULL,
  `bed_width` int(11) NOT NULL,
  `min_distance_between_pieces` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `rect_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_ip` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `submit_date` datetime DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
