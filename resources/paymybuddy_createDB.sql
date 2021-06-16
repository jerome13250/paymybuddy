------------------------------------------------------------
-- Database paymybuddy creation
------------------------------------------------------------

-- Listage de la structure de la base pour paymybuddy
CREATE DATABASE IF NOT EXISTS `paymybuddy`;
USE `paymybuddy`;

-- Listage de la structure de la table paymybuddy. role
CREATE TABLE IF NOT EXISTS `role` (
  `id` INT UNSIGNED UNSIGNED NOT NULL AUTO_INCREMENT,
  `rolename` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy.role 
INSERT INTO `role` (`id`, `rolename`) VALUES
	(1, 'USER'),
	(2, 'ADMIN');

-- Listage de la structure de la table paymybuddy. user
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `firstname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `lastname` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `inscriptionDateTime` datetime NOT NULL,
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `bankaccountnumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_email` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage de la structure de la table paymybuddy. transactions_bank
CREATE TABLE IF NOT EXISTS `transactions_bank` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `bankaccountnumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `datetime` datetime NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `FK_transactions_bank_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage de la structure de la table paymybuddy. transactions_user
CREATE TABLE IF NOT EXISTS `transactions_user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `usersource_id` INT UNSIGNED NOT NULL,
  `userdestination_id` INT UNSIGNED NOT NULL,
  `dateTime` datetime NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` char(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fees` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_tu_userdestinationid` (`userdestination_id`) USING BTREE,
  KEY `FK_tu_usersourceid` (`usersource_id`) USING BTREE,
  CONSTRAINT `FK_transactions_user_paymybuddy.user` FOREIGN KEY (`userdestination_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_transactions_user_paymybuddy.user_2` FOREIGN KEY (`usersource_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage de la structure de la table paymybuddy. user_connections
CREATE TABLE IF NOT EXISTS `user_connections` (
  `user_id` INT UNSIGNED NOT NULL,
  `connection_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`user_id`,`connection_id`) USING BTREE,
  KEY `FK_userconnections_connectionid` (`connection_id`) USING BTREE,
  CONSTRAINT `FK_user_connections_paymybuddy.user` FOREIGN KEY (`connection_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_connections_paymybuddy.user_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage de la structure de la table paymybuddy. user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `users_id` INT UNSIGNED NOT NULL,
  `roles_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`users_id`,`roles_id`) USING BTREE,
  KEY `fk_roles_id` (`roles_id`) USING BTREE,
  CONSTRAINT `FK_user_roles_paymybuddy.role` FOREIGN KEY (`roles_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK_user_roles_paymybuddy.user` FOREIGN KEY (`users_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


