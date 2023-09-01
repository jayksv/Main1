-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 29, 2023 at 09:02 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `auto_gigb_v2`
--

-- --------------------------------------------------------

--
-- Table structure for table `booking_status`
--

CREATE TABLE `booking_status` (
  `status_id` int(11) NOT NULL,
  `status_name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `login_log`
--

CREATE TABLE `login_log` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `login_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `ip_address` varchar(45) NOT NULL,
  `device_info` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `point`
--

CREATE TABLE `point` (
  `point_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `points` int(11) NOT NULL,
  `reason` varchar(100) DEFAULT NULL,
  `earned_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `product_color`
--

CREATE TABLE `product_color` (
  `color_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `color_name` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `product_color`
--

INSERT INTO `product_color` (`color_id`, `product_id`, `color_name`, `created_at`) VALUES
(19, 31, 'Red', '2023-08-29 03:00:33'),
(20, 31, 'Blue', '2023-08-29 03:00:33'),
(21, 32, 'Red', '2023-08-29 03:08:48'),
(22, 32, 'Blue', '2023-08-29 03:08:48'),
(23, 33, 'Red', '2023-08-29 05:47:36'),
(24, 33, 'Blue', '2023-08-29 05:47:36'),
(25, 34, 'Red', '2023-08-29 05:50:39'),
(26, 34, 'Blue', '2023-08-29 05:50:39'),
(27, 35, 'Red', '2023-08-29 05:55:51'),
(28, 35, 'Blue', '2023-08-29 05:55:51'),
(29, 36, 'Red', '2023-08-29 06:01:42'),
(30, 36, 'Blue', '2023-08-29 06:01:42'),
(31, 37, 'Red', '2023-08-29 06:20:09'),
(32, 37, 'Blue', '2023-08-29 06:20:09');

-- --------------------------------------------------------

--
-- Table structure for table `product_hashtag`
--

CREATE TABLE `product_hashtag` (
  `size_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `tag` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `product_images`
--

CREATE TABLE `product_images` (
  `image_id` int(11) NOT NULL,
  `product_image_path` varchar(150) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `createAt` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_images`
--

INSERT INTO `product_images` (`image_id`, `product_image_path`, `product_id`, `createAt`) VALUES
(6, 'product_image.png', 31, '2023-08-29 10:00:33'),
(7, 'product_image.png', 31, '2023-08-29 10:00:33'),
(8, 'product_image.png', 32, '2023-08-29 10:08:48'),
(9, 'product_image.png', 32, '2023-08-29 10:08:48'),
(10, 'product_image.png', 33, '2023-08-29 12:47:36'),
(11, 'product_image.png', 33, '2023-08-29 12:47:36'),
(12, 'product_image.png', 34, '2023-08-29 12:50:39'),
(13, 'product_image.png', 34, '2023-08-29 12:50:39'),
(14, 'product_image.png', 35, '2023-08-29 12:55:51'),
(15, 'product_image.png', 35, '2023-08-29 12:55:51'),
(16, 'product_image.png', 36, '2023-08-29 13:01:42'),
(17, 'product_image.png', 36, '2023-08-29 13:01:42'),
(18, 'product_image.png', 37, '2023-08-29 13:20:09'),
(19, 'product_image.png', 37, '2023-08-29 13:20:09');

-- --------------------------------------------------------

--
-- Table structure for table `product_size`
--

CREATE TABLE `product_size` (
  `size_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `size_name` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `product_size`
--

INSERT INTO `product_size` (`size_id`, `product_id`, `size_name`, `created_at`) VALUES
(3, 25, 'Small', '2023-08-28 09:59:12'),
(4, 25, 'Medium', '2023-08-28 09:59:12'),
(5, 26, 'Small', '2023-08-28 10:01:26'),
(6, 26, 'Medium', '2023-08-28 10:01:26'),
(7, 28, 'Small', '2023-08-28 10:06:26'),
(8, 28, 'Medium', '2023-08-28 10:06:26'),
(9, 29, 'Small', '2023-08-28 10:07:10'),
(10, 29, 'Medium', '2023-08-28 10:07:10'),
(13, 31, 'Small', '2023-08-29 03:00:33'),
(14, 31, 'Medium', '2023-08-29 03:00:33'),
(15, 32, 'Small', '2023-08-29 03:08:48'),
(16, 32, 'Medium', '2023-08-29 03:08:48'),
(17, 33, 'Small', '2023-08-29 05:47:36'),
(18, 33, 'Medium', '2023-08-29 05:47:36'),
(19, 34, 'Small', '2023-08-29 05:50:39'),
(20, 34, 'Medium', '2023-08-29 05:50:39'),
(21, 35, 'Small', '2023-08-29 05:55:51'),
(22, 35, 'Medium', '2023-08-29 05:55:51'),
(23, 36, 'Small', '2023-08-29 06:01:42'),
(24, 36, 'Medium', '2023-08-29 06:01:42'),
(25, 37, 'Small', '2023-08-29 06:20:09'),
(26, 37, 'Medium', '2023-08-29 06:20:09');

-- --------------------------------------------------------

--
-- Table structure for table `shop_status`
--

CREATE TABLE `shop_status` (
  `status_id` int(11) NOT NULL,
  `status_name` varchar(50) NOT NULL,
  `status_description` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shop_status`
--

INSERT INTO `shop_status` (`status_id`, `status_name`, `status_description`) VALUES
(1, 'Pending', NULL),
(2, 'Approved', NULL),
(3, 'Rejected', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tb_address`
--

CREATE TABLE `tb_address` (
  `address_id` int(11) NOT NULL,
  `street_address` varchar(100) NOT NULL,
  `state` varchar(50) DEFAULT NULL,
  `postal_code` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_address`
--

INSERT INTO `tb_address` (`address_id`, `street_address`, `state`, `postal_code`, `country`, `latitude`, `longitude`, `create_at`) VALUES
(32, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, '2023-08-19 08:40:54'),
(37, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(38, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(39, '5678 Main St', 'California 589', '12345 AABB', 'USA Country', NULL, NULL, NULL),
(40, '', '', '', '', 0.00000000, 0.00000000, NULL),
(41, '', '', '', '', 0.00000000, 0.00000000, NULL),
(42, '123 Main St', 'California', '12345', 'LAOS', 37.00000000, 122.00000000, NULL),
(43, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(44, '', '', '', '', 0.00000000, 0.00000000, NULL),
(45, '', '', '', '', 0.00000000, 0.00000000, NULL),
(46, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(47, '', '', '', '', 0.00000000, 0.00000000, NULL),
(48, '', '', '', '', 0.00000000, 0.00000000, NULL),
(49, '', '', '', '', NULL, NULL, NULL),
(50, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(51, '', '', '', '', 0.00000000, 0.00000000, NULL),
(52, '', '', '', '', NULL, NULL, NULL),
(53, '', '', '', '', 0.00000000, 0.00000000, NULL),
(54, 'California', NULL, NULL, NULL, NULL, NULL, NULL),
(55, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(56, 'California', NULL, NULL, NULL, NULL, NULL, NULL),
(57, '', '', '', '', 0.00000000, 0.00000000, NULL),
(58, '', '', '', '', NULL, NULL, NULL),
(59, '', '', '', '', 0.00000000, 0.00000000, NULL),
(60, '123 Main St', 'California', '12345', 'USA', 37.00000000, 122.00000000, NULL),
(61, '123 Main St', 'California', '12345', 'USA', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tb_booking`
--

CREATE TABLE `tb_booking` (
  `booking_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `service_id` int(11) DEFAULT NULL,
  `shop_id` int(11) DEFAULT NULL,
  `booking_date` date NOT NULL,
  `booking_time` time NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tb_categories`
--

CREATE TABLE `tb_categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `category_icon` varchar(150) DEFAULT 'https://cdn-icons-png.flaticon.com/512/9573/9573279.png',
  `description` text DEFAULT NULL,
  `parent_category_id` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_categories`
--

INSERT INTO `tb_categories` (`category_id`, `category_name`, `category_icon`, `description`, `parent_category_id`, `created_at`, `updated_at`) VALUES
(3, 'for oil', 'https://cdn-icons-png.flaticon.com/512/9573/9573279.png', 'user other car', 0, '2023-08-28 07:06:42', '2023-08-28 07:06:42'),
(4, 'car oil', 'https://cdn-icons-png.flaticon.com/512/9573/9573279.png', 'user other car', 3, '2023-08-29 03:56:09', '2023-08-29 03:56:09'),
(5, 'color', 'https://cdn-icons-png.flaticon.com/512/9573/9573279.png', 'user other car', 0, '2023-08-29 03:56:24', '2023-08-29 03:56:24');

-- --------------------------------------------------------

--
-- Table structure for table `tb_comments`
--

CREATE TABLE `tb_comments` (
  `comment_id` int(11) NOT NULL,
  `rating` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `shop_id` int(11) DEFAULT NULL,
  `parent_comment_id` int(11) DEFAULT NULL,
  `comment_text` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tb_coupons`
--

CREATE TABLE `tb_coupons` (
  `coupon_id` int(11) NOT NULL,
  `coupon_code` varchar(20) NOT NULL,
  `discount` decimal(5,2) NOT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_until` date DEFAULT NULL,
  `max_usage` int(11) DEFAULT NULL,
  `remaining_usage` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `shop_id` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tb_products`
--

CREATE TABLE `tb_products` (
  `product_id` int(11) NOT NULL,
  `description` text DEFAULT NULL,
  `product_name` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `stock_quantity` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `user_id` int(11) DEFAULT NULL,
  `shope_id` int(11) DEFAULT NULL,
  `product_image` varchar(150) DEFAULT NULL,
  `product_any_image` varchar(150) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_products`
--

INSERT INTO `tb_products` (`product_id`, `description`, `product_name`, `price`, `stock_quantity`, `created_at`, `user_id`, `shope_id`, `product_image`, `product_any_image`, `category_id`) VALUES
(31, 'Example product description', 'Example Product1', 299.99, 100, '2023-08-29 03:00:33', 19, 4, 'example_image.jpg', NULL, 3),
(32, 'Example product description', 'Example Product', 29.99, 100, '2023-08-29 03:08:48', 19, 4, 'example_image.jpg', NULL, 3),
(35, 'Example product description2', 'Example Product2', 29911.99, 10, '2023-08-29 05:55:51', NULL, NULL, 'example_image.jpg', NULL, 3),
(36, 'Example product description2', 'Example Product2', 29911.99, 10, '2023-08-29 06:01:42', NULL, NULL, 'example_image.jpg', NULL, 3),
(37, 'Example product description2', 'Example Product2', 29911.99, 10, '2023-08-29 06:20:09', NULL, NULL, 'example_image.jpg', NULL, 3);

-- --------------------------------------------------------

--
-- Table structure for table `tb_role`
--

CREATE TABLE `tb_role` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_role`
--

INSERT INTO `tb_role` (`role_id`, `role_name`, `description`, `created_at`) VALUES
(1, 'Super Admin', 'Main of admin office', '2023-08-15 04:11:51'),
(2, 'admin', NULL, '2023-08-15 05:24:43'),
(3, 'shop owner', NULL, '2023-08-15 05:24:43'),
(4, 'General user', NULL, '2023-08-15 05:25:07');

-- --------------------------------------------------------

--
-- Table structure for table `tb_service`
--

CREATE TABLE `tb_service` (
  `service_id` int(11) NOT NULL,
  `shop_id` int(11) DEFAULT NULL,
  `service_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `duration_minutes` int(11) NOT NULL,
  `createBy` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tb_service_type`
--

CREATE TABLE `tb_service_type` (
  `serviceType_id` int(11) NOT NULL,
  `type_name` varchar(100) NOT NULL,
  `parent_type_id` int(11) DEFAULT NULL,
  `createAt` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tb_shop`
--

CREATE TABLE `tb_shop` (
  `shop_id` int(11) NOT NULL,
  `shop_name` varchar(100) NOT NULL,
  `street_address` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `shop_type_id` int(11) DEFAULT NULL,
  `shop_image` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `shop_status_id` int(11) DEFAULT 1,
  `monday_open` time DEFAULT NULL,
  `monday_close` time DEFAULT NULL,
  `tuesday_open` time DEFAULT NULL,
  `tuesday_close` time DEFAULT NULL,
  `wednesday_open` time DEFAULT NULL,
  `wednesday_close` time DEFAULT NULL,
  `thursday_open` time DEFAULT NULL,
  `thursday_close` time DEFAULT NULL,
  `friday_open` time DEFAULT NULL,
  `friday_close` time DEFAULT NULL,
  `saturday_open` time DEFAULT NULL,
  `saturday_close` time DEFAULT NULL,
  `sunday_open` time DEFAULT NULL,
  `sunday_close` time DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_shop`
--

INSERT INTO `tb_shop` (`shop_id`, `shop_name`, `street_address`, `city`, `state`, `postal_code`, `country`, `latitude`, `longitude`, `shop_type_id`, `shop_image`, `shop_status_id`, `monday_open`, `monday_close`, `tuesday_open`, `tuesday_close`, `wednesday_open`, `wednesday_close`, `thursday_open`, `thursday_close`, `friday_open`, `friday_close`, `saturday_open`, `saturday_close`, `sunday_open`, `sunday_close`, `created_at`) VALUES
(4, 'Example Shop', '123 Main St', 'Cityville', 'State', '12345', 'Country', 37.12345678, -122.98765432, 5, 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', 2, '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '10:00:00', '16:00:00', '10:00:00', '16:00:00', '2023-08-28 04:30:04'),
(5, 'Example Shop', '123 Main St', 'Cityville', 'State', '12345', 'Country', 37.12345678, -122.98765432, 5, 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', 2, '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '10:00:00', '16:00:00', '10:00:00', '16:00:00', '2023-08-28 06:43:52'),
(6, 'Example Shop', '123 Main St', 'Cityville', 'State', '12345', 'Country', 37.12345678, -122.98765432, 5, 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', 1, '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '10:00:00', '16:00:00', '10:00:00', '16:00:00', '2023-08-28 07:13:48'),
(7, 'Example Shop', '123 Main St', 'Cityville', 'State', '12345', 'Country', 37.12345678, -122.98765432, 1, 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', NULL, '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '10:00:00', '16:00:00', '10:00:00', '16:00:00', '2023-08-28 08:25:59'),
(8, 'Example Shop12', '123 Main St', 'Cityville', 'State', '12345', 'Country', 37.12345678, -122.98765432, 5, 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', 3, '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '08:00:00', '18:00:00', '10:00:00', '16:00:00', '10:00:00', '16:00:00', '2023-08-28 09:36:45');

-- --------------------------------------------------------

--
-- Table structure for table `tb_shop_types`
--

CREATE TABLE `tb_shop_types` (
  `type_id` int(11) NOT NULL,
  `type_name` varchar(50) NOT NULL,
  `type_icon` varchar(200) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_shop_types`
--

INSERT INTO `tb_shop_types` (`type_id`, `type_name`, `type_icon`, `created_at`) VALUES
(5, 'car', '', '2023-08-28 06:48:58');

-- --------------------------------------------------------

--
-- Table structure for table `tb_users`
--

CREATE TABLE `tb_users` (
  `user_id` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(150) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `birthdate` date DEFAULT NULL,
  `gender` enum('Male','Female','Other') DEFAULT NULL,
  `profile_picture` varchar(150) DEFAULT 'https://srcwap.com/wp-content/uploads/2022/08/stock-illustration-male-avatar-profile-picture-use.jpg',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` enum('Active','Pending','Block','') DEFAULT NULL,
  `bio` varchar(150) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  `vehicle_id` int(11) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  `shop_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `tb_users`
--

INSERT INTO `tb_users` (`user_id`, `email`, `password`, `first_name`, `last_name`, `birthdate`, `gender`, `profile_picture`, `created_at`, `last_login`, `is_active`, `bio`, `role_id`, `vehicle_id`, `address_id`, `shop_id`) VALUES
(19, 'example@example.com', '$2a$10$2aAhJNcFjqFLAnRV.EQHoOkrFTpjaCm2ykL/0YlvVXDsEBgSkQQWO', 'John1', 'Doe', '2000-01-01', 'Male', 'https://res.cloudinary.com/ds639zn4t/image/upload/v1692853317/jcrse5bt6nahopx3azfx.jpg', '2023-08-19 08:40:55', NULL, 'Active', 'A brief biography.', 1, NULL, 32, NULL),
(24, 'example11@example.com', '$2a$10$5rxb3d9l4OOgbzaEn1DGN.GBewPYn4UmQAnaSfs6qp5BRPCL8.Xnu', 'John', 'Doe', '2000-01-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-28 04:30:04', NULL, 'Active', 'A brief biography.', 3, NULL, 37, 4),
(25, 'exampl2@example.com', '$2a$10$3RQq4yZGm3vRZhDT6bdfIOwZPLohHbuX4oZ6ie3XwRXdr68yqtlD.', 'John', 'Doe', '2000-01-01', 'Male', 'https://res.cloudinary.com/ds639zn4t/image/upload/v1692855266/tix1azllg1qrkxvvmiip.jpg', '2023-08-28 05:52:01', NULL, 'Active', 'A brief biography.', 2, NULL, 38, NULL),
(26, 'upp9@example.com', '$2a$10$Zl9uvQ22RiTPTzq47j2ULOxz7kQ5ga24uylDaqz5OfLwg2ujs8B4O', 'Khensa  test', 'DPJdd', '2000-01-30', 'Male', NULL, '2023-08-28 06:26:25', NULL, 'Active', NULL, 4, NULL, 39, NULL),
(27, 'lolo@gmail.com', '$2a$10$lVwMVeVvMC6DePVMXElJl.RyiNxQJoRgseI9zJDN7xyR7oJHE.mCK', 'dsasd', 'wwaa', '2003-08-01', 'Female', '', '2023-08-28 06:28:16', NULL, 'Active', '', 4, NULL, 40, NULL),
(28, 'lolo123@gmail.com', '$2a$10$QdbE9xikPDl5mXLe3LqnkuUZTdniE3ronzqqLyHEWlTw29FuJ5kdK', 'dsasd', 'wwaa', '2003-08-01', 'Female', '', '2023-08-28 06:30:27', NULL, 'Active', '', 4, NULL, 41, NULL),
(29, 'u28@example.com', '$2a$10$En.r8SXH.ljPKZKgO9T/zuCkzzjDxsTEL5y9HwtJEqgvtnJU4LVVO', 'llllllllll', 'Doe', '2000-01-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-28 06:43:34', NULL, 'Active', 'A brief biography.', 4, NULL, 42, NULL),
(30, 'noi@example.com', '$2a$10$ddFcV8PEmTbG9oQHaEVhJ.AmF0RdWmeqGW9WJGi3FkyOWMUkaXXFK', 'John', 'Doe', '2000-01-01', 'Male', 'https://res.cloudinary.com/ds639zn4t/image/upload/v1693024738/sample.jpg', '2023-08-28 06:43:53', NULL, 'Active', 'A brief biography.', 3, NULL, 43, 5),
(31, 'popo@gmail.com', '$2a$10$TXFGt5PNPfSxx6yIHpSjoe67lifXnEY5vAk6i5gpQYfusfk8RelaW', 'popo', 'poco', '1999-08-01', 'Male', '', '2023-08-28 06:52:16', NULL, 'Active', '', 4, NULL, 44, NULL),
(32, 'lopo@gmail.com', '$2a$10$iFBU6.kuT.6g3Wrl1feKCubMNGeNPJjq9hy9zFqfue6kNCtnNXnYm', 'dsadw', 'dasw', '1996-08-28', 'Male', '', '2023-08-28 06:52:57', NULL, 'Active', '', 4, NULL, 45, NULL),
(33, 'example50@example.com', '$2a$10$LrggLHpu73ScdS948xT.uOBmifAl4W8spCeYGDcFXvR8gM4mjd5Be', 'John', 'Doe', '2000-01-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-28 07:13:49', NULL, 'Active', 'A brief biography.', 3, NULL, 46, 6),
(34, 'joio@gmail.com', '$2a$10$CpOfpdboZECKa/DYuUKbLu1iDKeieymjwUgQKLSIT6M5Mc7Pk/wxG', 'peter', 'asda', '2012-08-28', 'Male', '', '2023-08-28 07:30:46', NULL, 'Active', '', 4, NULL, 47, NULL),
(35, 'joio23@gmail.com', '$2a$10$0ZPDUoMV9Ot2IhiIY9qgnu6Xtu8.YBdVNhXQ1l3P7wCAEgZ7AT2vK', 'peter', 'asda', '2012-08-28', 'Male', '', '2023-08-28 07:32:20', NULL, 'Active', '', 4, NULL, 48, NULL),
(36, 'exampl3@example.com', '$2a$10$Ii2tHrvnKRR7a9P.ElV/0eKCz9azPYJYI51BIhOAlne6Iqj7b8MFu', 'John', 'Doe', '2000-01-01', 'Male', 'https://res.cloudinary.com/ds639zn4t/image/upload/v1692855266/tix1azllg1qrkxvvmiip.jpg', '2023-08-28 07:34:35', NULL, 'Active', 'A brief biography.', 2, NULL, 49, NULL),
(37, 'example5@example.com', '$2a$10$ppijNb3LBbIR8cle4aNGNegWAog1SYLEr.qoTr5yDX2Xz7CrfwQRu', 'John', 'Doe', '2000-01-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-28 08:25:59', NULL, 'Active', 'A brief biography.', 3, NULL, 50, 7),
(38, 'sa@gmail.com', '$2a$10$C5oPx.HsPVJYdOxQnrI1mOyfkkspLvjJEb6poq/a7aIOdzHffdLgi', 'dsa', 'awaw', '2023-08-28', 'Other', '', '2023-08-28 09:26:07', NULL, 'Active', '', 4, NULL, 51, NULL),
(39, 'exampl5@example.com', '$2a$10$XzUrbyorg2Kj7zplo.orGOFCtLUuIz229.juwlQTWCsc5XqtfUgWi', 'John', 'Doe', NULL, 'Male', '', '2023-08-28 09:26:27', NULL, 'Active', '', 2, NULL, 52, NULL),
(40, 'sa66@gmail.com', '$2a$10$fOh0DlIMHBBX/Ekp/M5YgOnB6fhdhagP70ZGtW2xbTnXjiokV5XQa', 'adsa', 'sdas', '2023-08-28', 'Male', '', '2023-08-28 09:33:49', NULL, 'Active', '', 4, NULL, 53, NULL),
(41, 'exampl52@example.com', '$2a$10$rdVVk2cQYJ5GogP0YDdGNOOii8v4gAGNchXjC4WDpxl6JqlQoTam.', 'John', 'Doe', NULL, 'Male', NULL, '2023-08-28 09:33:54', NULL, 'Active', NULL, 2, NULL, 54, NULL),
(42, 'example55@example.com', '$2a$10$.KFJLRI9BfJxMADQtVxFTODhHLOiPJnjxjIK5p8FSDXKMdsPtb.NK', 'John', 'Doe', '2000-01-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-28 09:36:45', NULL, 'Active', 'A brief biography.', 3, NULL, 55, 8),
(43, 'exampl32@example.com', '$2a$10$r5DRfOvqA8eJ1OvZfEFNo.1ZquLIXFJ.5w36kMp/EahdrhlZaAUji', 'John', 'Doe', '2023-08-11', 'Male', NULL, '2023-08-28 09:42:40', NULL, 'Active', NULL, 2, NULL, 56, NULL),
(44, 'dew@gmail.com', '$2a$10$jdOtWtYMR991QXlh99tR4essbXZPFHgSyK1mokpo5FFun/4RC1/We', 'dew', 'dadda', '1997-08-16', 'Female', '', '2023-08-29 02:13:07', NULL, 'Active', '', 4, NULL, 57, NULL),
(45, 'peter10@gmail.com', '$2a$10$E/IGB48rVLetmounqB7XSOzeifVcR8H7VxpBuDPqb5BEmewyMlwOm', 'peter LL', 'alolo', '1994-11-02', 'Male', NULL, '2023-08-29 02:17:55', NULL, 'Active', NULL, 4, NULL, 58, NULL),
(46, 'phet12@gmail.com', '$2a$10$2u9LdJHqRggtimHkL5zKhO7ix7RHL1I1NYpKKpQuioxsVjMFRGiUy', 'phet', 'namphet', '2023-08-29', 'Female', '', '2023-08-29 03:36:30', NULL, 'Active', NULL, 4, NULL, 59, NULL),
(47, 'us1@example.com', '$2a$10$h/234yurXp6X88rYNcD3cOwpchTxdEnzg7SvTgSzxzTuIranBph6K', 'hghghghg', 'Doe', '2000-03-01', 'Male', 'C:\\Auto Office\\Project\\GIGI_admin\\src\\main\\resources\\static\\uploaded\\images\\7985802c-bea3-46ea-a32e-d39ef5d5e616.png', '2023-08-29 03:44:12', NULL, 'Active', 'A brief biography.', 4, NULL, 60, NULL),
(48, 'Khensa@gmail.com', '$2a$10$iQInX3Jv57k7KKqNaFbqO.FWM/rz/ymfMAZvfE3kXp3e3P3CwzXbO', 'Khensa', 'DPJ', '2000-03-01', 'Other', NULL, '2023-08-29 04:19:18', NULL, 'Active', NULL, 4, NULL, 61, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tb_vehicles`
--

CREATE TABLE `tb_vehicles` (
  `vehicle_id` int(11) DEFAULT NULL,
  `model` varchar(50) NOT NULL,
  `year` int(11) NOT NULL,
  `color` varchar(50) NOT NULL,
  `vehicle_type` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_shop_view`
-- (See below for the actual view)
--
CREATE TABLE `user_shop_view` (
`user_id` int(11)
,`email` varchar(50)
,`first_name` varchar(50)
,`last_name` varchar(50)
,`birthdate` date
,`gender` enum('Male','Female','Other')
,`profile_picture` varchar(150)
,`created_at` timestamp
,`last_login` timestamp
,`is_active` enum('Active','Pending','Block','')
,`bio` varchar(150)
,`role_id` int(11)
,`role_name` varchar(50)
,`user_address` varchar(100)
,`user_state` varchar(50)
,`user_postal_code` varchar(50)
,`user_country` varchar(50)
,`user_latitude` decimal(10,8)
,`user_longitude` decimal(11,8)
,`shop_id` int(11)
,`shop_name` varchar(100)
,`shop_address` varchar(100)
,`city` varchar(50)
,`shop_state` varchar(50)
,`shop_postal_code` varchar(20)
,`shop_country` varchar(50)
,`shop_latitude` decimal(10,8)
,`shop_longitude` decimal(11,8)
,`type_id` int(11)
,`shop_image` varchar(150)
,`monday_open` time
,`monday_close` time
,`tuesday_open` time
,`tuesday_close` time
,`wednesday_open` time
,`wednesday_close` time
,`thursday_open` time
,`thursday_close` time
,`friday_open` time
,`friday_close` time
,`saturday_open` time
,`saturday_close` time
,`sunday_open` time
,`sunday_close` time
,`shop_created_at` timestamp
);

-- --------------------------------------------------------

--
-- Structure for view `user_shop_view`
--
DROP TABLE IF EXISTS `user_shop_view`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_shop_view`  AS SELECT `tb_users`.`user_id` AS `user_id`, `tb_users`.`email` AS `email`, `tb_users`.`first_name` AS `first_name`, `tb_users`.`last_name` AS `last_name`, `tb_users`.`birthdate` AS `birthdate`, `tb_users`.`gender` AS `gender`, `tb_users`.`profile_picture` AS `profile_picture`, `tb_users`.`created_at` AS `created_at`, `tb_users`.`last_login` AS `last_login`, `tb_users`.`is_active` AS `is_active`, `tb_users`.`bio` AS `bio`, `tb_users`.`role_id` AS `role_id`, `tb_role`.`role_name` AS `role_name`, `tb_address`.`street_address` AS `user_address`, `tb_address`.`state` AS `user_state`, `tb_address`.`postal_code` AS `user_postal_code`, `tb_address`.`country` AS `user_country`, `tb_address`.`latitude` AS `user_latitude`, `tb_address`.`longitude` AS `user_longitude`, `tb_shop`.`shop_id` AS `shop_id`, `tb_shop`.`shop_name` AS `shop_name`, `tb_shop`.`street_address` AS `shop_address`, `tb_shop`.`city` AS `city`, `tb_shop`.`state` AS `shop_state`, `tb_shop`.`postal_code` AS `shop_postal_code`, `tb_shop`.`country` AS `shop_country`, `tb_shop`.`latitude` AS `shop_latitude`, `tb_shop`.`longitude` AS `shop_longitude`, `tb_shop`.`shop_type_id` AS `type_id`, `tb_shop`.`shop_image` AS `shop_image`, `tb_shop`.`monday_open` AS `monday_open`, `tb_shop`.`monday_close` AS `monday_close`, `tb_shop`.`tuesday_open` AS `tuesday_open`, `tb_shop`.`tuesday_close` AS `tuesday_close`, `tb_shop`.`wednesday_open` AS `wednesday_open`, `tb_shop`.`wednesday_close` AS `wednesday_close`, `tb_shop`.`thursday_open` AS `thursday_open`, `tb_shop`.`thursday_close` AS `thursday_close`, `tb_shop`.`friday_open` AS `friday_open`, `tb_shop`.`friday_close` AS `friday_close`, `tb_shop`.`saturday_open` AS `saturday_open`, `tb_shop`.`saturday_close` AS `saturday_close`, `tb_shop`.`sunday_open` AS `sunday_open`, `tb_shop`.`sunday_close` AS `sunday_close`, `tb_shop`.`created_at` AS `shop_created_at` FROM (((`tb_users` left join `tb_role` on(`tb_users`.`role_id` = `tb_role`.`role_id`)) left join `tb_shop` on(`tb_users`.`shop_id` = `tb_shop`.`shop_id`)) left join `tb_address` on(`tb_users`.`address_id` = `tb_address`.`address_id`)) WHERE `tb_users`.`role_id` = 3 ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `booking_status`
--
ALTER TABLE `booking_status`
  ADD PRIMARY KEY (`status_id`);

--
-- Indexes for table `login_log`
--
ALTER TABLE `login_log`
  ADD PRIMARY KEY (`log_id`);

--
-- Indexes for table `point`
--
ALTER TABLE `point`
  ADD PRIMARY KEY (`point_id`);

--
-- Indexes for table `product_color`
--
ALTER TABLE `product_color`
  ADD PRIMARY KEY (`color_id`);

--
-- Indexes for table `product_hashtag`
--
ALTER TABLE `product_hashtag`
  ADD PRIMARY KEY (`size_id`);

--
-- Indexes for table `product_images`
--
ALTER TABLE `product_images`
  ADD PRIMARY KEY (`image_id`);

--
-- Indexes for table `product_size`
--
ALTER TABLE `product_size`
  ADD PRIMARY KEY (`size_id`);

--
-- Indexes for table `shop_status`
--
ALTER TABLE `shop_status`
  ADD PRIMARY KEY (`status_id`);

--
-- Indexes for table `tb_address`
--
ALTER TABLE `tb_address`
  ADD PRIMARY KEY (`address_id`);

--
-- Indexes for table `tb_booking`
--
ALTER TABLE `tb_booking`
  ADD PRIMARY KEY (`booking_id`);

--
-- Indexes for table `tb_categories`
--
ALTER TABLE `tb_categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `tb_comments`
--
ALTER TABLE `tb_comments`
  ADD PRIMARY KEY (`comment_id`);

--
-- Indexes for table `tb_coupons`
--
ALTER TABLE `tb_coupons`
  ADD PRIMARY KEY (`coupon_id`);

--
-- Indexes for table `tb_products`
--
ALTER TABLE `tb_products`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `tb_role`
--
ALTER TABLE `tb_role`
  ADD PRIMARY KEY (`role_id`);

--
-- Indexes for table `tb_service`
--
ALTER TABLE `tb_service`
  ADD PRIMARY KEY (`service_id`);

--
-- Indexes for table `tb_service_type`
--
ALTER TABLE `tb_service_type`
  ADD PRIMARY KEY (`serviceType_id`);

--
-- Indexes for table `tb_shop`
--
ALTER TABLE `tb_shop`
  ADD PRIMARY KEY (`shop_id`);

--
-- Indexes for table `tb_shop_types`
--
ALTER TABLE `tb_shop_types`
  ADD PRIMARY KEY (`type_id`);

--
-- Indexes for table `tb_users`
--
ALTER TABLE `tb_users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `booking_status`
--
ALTER TABLE `booking_status`
  MODIFY `status_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `login_log`
--
ALTER TABLE `login_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `point`
--
ALTER TABLE `point`
  MODIFY `point_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `product_color`
--
ALTER TABLE `product_color`
  MODIFY `color_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `product_hashtag`
--
ALTER TABLE `product_hashtag`
  MODIFY `size_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `product_images`
--
ALTER TABLE `product_images`
  MODIFY `image_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `product_size`
--
ALTER TABLE `product_size`
  MODIFY `size_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `shop_status`
--
ALTER TABLE `shop_status`
  MODIFY `status_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tb_address`
--
ALTER TABLE `tb_address`
  MODIFY `address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT for table `tb_booking`
--
ALTER TABLE `tb_booking`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tb_categories`
--
ALTER TABLE `tb_categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tb_comments`
--
ALTER TABLE `tb_comments`
  MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tb_coupons`
--
ALTER TABLE `tb_coupons`
  MODIFY `coupon_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tb_products`
--
ALTER TABLE `tb_products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `tb_role`
--
ALTER TABLE `tb_role`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tb_service`
--
ALTER TABLE `tb_service`
  MODIFY `service_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tb_service_type`
--
ALTER TABLE `tb_service_type`
  MODIFY `serviceType_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tb_shop`
--
ALTER TABLE `tb_shop`
  MODIFY `shop_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tb_shop_types`
--
ALTER TABLE `tb_shop_types`
  MODIFY `type_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tb_users`
--
ALTER TABLE `tb_users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
