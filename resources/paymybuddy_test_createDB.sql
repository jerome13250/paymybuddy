------------------------------------------------------------
-- Database paymybuddy_test creation
-- This database exists for integration test only
------------------------------------------------------------

-- Listage de la structure de la base pour paymybuddy_test
CREATE DATABASE IF NOT EXISTS `paymybuddy_test`;
USE `paymybuddy_test`;

-- Listage de la structure de la table paymybuddy_test. role
CREATE TABLE IF NOT EXISTS `role` (
  `id` INT UNSIGNED UNSIGNED NOT NULL AUTO_INCREMENT,
  `rolename` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy_test.role 
INSERT INTO `role` (`id`, `rolename`) VALUES
	(1, 'USER'),
	(2, 'ADMIN');

-- Listage de la structure de la table paymybuddy_test. user
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

-- Listage des données de la table paymybuddy_test_test.user : 
INSERT INTO `user` (`id`, `firstname`, `lastname`, `email`, `inscriptionDateTime`, `password`, `enabled`, `bankaccountnumber`, `amount`, `currency`) VALUES
	(53, 'firstname', 'lastname', 'test@mail.com', '2021-05-27 20:40:53', '$2a$10$TvbN4daKBDjT1OK2GQQfT.JtgGHEWPJbwqbeuVEKIi72u11iKmdX2', 1, '123456789', 1000.00, 'USD'),
	(54, 'firstname54', 'lastname54', 'anothertest@mail.com', '2024-12-31 23:45:00', 'password54', 1, '1AX256', 1000.00, 'USD');

-- Listage de la structure de la table paymybuddy_test. transactions_bank
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

-- Listage de la structure de la table paymybuddy_test. transactions_user
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
  CONSTRAINT `FK_transactions_user_paymybuddy_test.user` FOREIGN KEY (`userdestination_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_transactions_user_paymybuddy_test.user_2` FOREIGN KEY (`usersource_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage de la structure de la table paymybuddy_test. user_connections
CREATE TABLE IF NOT EXISTS `user_connections` (
  `user_id` INT UNSIGNED NOT NULL,
  `connection_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`user_id`,`connection_id`) USING BTREE,
  KEY `FK_userconnections_connectionid` (`connection_id`) USING BTREE,
  CONSTRAINT `FK_user_connections_paymybuddy_test.user` FOREIGN KEY (`connection_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_connections_paymybuddy_test.user_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy_test_test.user_connections : ~2 rows (environ)
INSERT INTO `user_connections` (`user_id`, `connection_id`) VALUES
	(53, 54);

-- Listage de la structure de la table paymybuddy_test. user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `users_id` INT UNSIGNED NOT NULL,
  `roles_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`users_id`,`roles_id`) USING BTREE,
  KEY `fk_roles_id` (`roles_id`) USING BTREE,
  CONSTRAINT `FK_user_roles_paymybuddy_test.role` FOREIGN KEY (`roles_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FK_user_roles_paymybuddy_test.user` FOREIGN KEY (`users_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table paymybuddy_test.user_roles : ~1 rows (environ)
INSERT INTO `user_roles` (`users_id`, `roles_id`) VALUES
	(53, 1),
	(54, 1);
