DROP TABLE IF EXISTS `issue`;
DROP TABLE IF EXISTS `developer`;

CREATE TABLE `developer` (
    `id` int(11) NOT NULL,
    `name` varchar(255) DEFAULT NULL
);


ALTER TABLE `developer`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `developer`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

CREATE TABLE `issue` (
    `issue_type` varchar(31) NOT NULL,
    `id` int(11) NOT NULL,
    `created_on` date DEFAULT curdate(),
    `description` varchar(255) DEFAULT NULL,
    `title` varchar(255) DEFAULT NULL,
    `estimated_point_value` int(11) DEFAULT NULL,
    `status` varchar(255) NOT NULL,
    `priority` int(11) DEFAULT NULL,
    `developer_id` int(11) DEFAULT NULL
);

ALTER TABLE `issue`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `issue`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;