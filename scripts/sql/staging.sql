--
-- PostgreSQL database dump
--

\restrict HpDzzAM6O1Nw2UIkqjA5LLmBprbfsbEAd7c02DkiwD2m9xQI2uyrSmE3vDX4gFn

-- Dumped from database version 16.13 (Debian 16.13-1.pgdg13+1)
-- Dumped by pg_dump version 16.13 (Debian 16.13-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: deporte; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.deporte VALUES ('d7f59082-6c39-41d3-9fa9-f6d23607d2e8', 'Fútbol');
INSERT INTO public.deporte VALUES ('1e719ff6-fc61-47a6-bc7c-1acc7dc29175', 'Tenis');
INSERT INTO public.deporte VALUES ('42220c3f-625b-4a39-8d02-84575a7edf2a', 'Paddle');
INSERT INTO public.deporte VALUES ('036a50b6-21fd-4240-b0cd-495aeef0bad1', 'Vóley');
INSERT INTO public.deporte VALUES ('c7aa4383-df6e-4630-8b81-e7938abc1917', 'Básquet');


--
-- Data for Name: local; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.local VALUES ('89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd', NULL, 'Rivadavia 543', NULL, 'Arena Punto', NULL);
INSERT INTO public.local VALUES ('9fa858a1-a170-4e57-83da-ba8c448f7bc7', NULL, 'Entre Ríos 345', NULL, 'Arena Patagonia', NULL);
INSERT INTO public.local VALUES ('ad0dd867-1120-42b4-b20b-fb297c314e74', NULL, 'San Martín 567', NULL, 'Center Punto', NULL);
INSERT INTO public.local VALUES ('a7d134bb-c510-49e1-9cd3-ebe1620170d5', NULL, 'Pueyrredón 432', NULL, 'Sports Gol', NULL);
INSERT INTO public.local VALUES ('988f5f45-a4ad-4b6f-8784-e84a4e28aff0', NULL, 'Entre Ríos 345', NULL, 'Zona Patagonia', NULL);


--
-- Data for Name: cancha; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.cancha VALUES ('19dba84a-ad98-4b70-9fca-b20475265d0c', 4, 'Cancha 2 - Exterior', '1e719ff6-fc61-47a6-bc7c-1acc7dc29175', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.cancha VALUES ('53626c0d-4458-45dd-8868-4ec54e21af48', 10, 'Cancha 3 - Exterior', 'd7f59082-6c39-41d3-9fa9-f6d23607d2e8', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.cancha VALUES ('439dddc4-93b6-4fa1-96f1-66a2aa350a2e', 10, 'Cancha 2 - Sintética', 'c7aa4383-df6e-4630-8b81-e7938abc1917', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.cancha VALUES ('bb6a0e41-1ffa-45dd-857d-91e3db1cb197', 12, 'Cancha 3 - Exterior', '036a50b6-21fd-4240-b0cd-495aeef0bad1', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.cancha VALUES ('260a2877-d18d-4b51-b752-83d2a1d8fb99', 4, 'Cancha 2 - Techada', '1e719ff6-fc61-47a6-bc7c-1acc7dc29175', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.cancha VALUES ('7a8932d3-8f05-4099-bdc0-cb48921e6deb', 10, 'Cancha 3 - Techada', 'c7aa4383-df6e-4630-8b81-e7938abc1917', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.cancha VALUES ('8b0f6333-a728-4311-a05e-11717adf150b', 4, 'Cancha 2 - Premium', '42220c3f-625b-4a39-8d02-84575a7edf2a', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.cancha VALUES ('6cef91de-a8ae-45e4-92ea-50b8b99f6b76', 10, 'Cancha 3 - Premium', 'd7f59082-6c39-41d3-9fa9-f6d23607d2e8', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.cancha VALUES ('0653f787-c5a0-42af-b123-3ee5c6800cf0', 4, 'Cancha 2 - Premium', '1e719ff6-fc61-47a6-bc7c-1acc7dc29175', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.cancha VALUES ('615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8', 4, 'Cancha 3 - Premium', '1e719ff6-fc61-47a6-bc7c-1acc7dc29175', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');


--
-- Data for Name: configuracion_horario; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.configuracion_horario VALUES ('bb1f46ee-b126-48f6-87f4-8296e8164a2e', true, 1800000000000, '19dba84a-ad98-4b70-9fca-b20475265d0c');
INSERT INTO public.configuracion_horario VALUES ('bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27', true, 1800000000000, '53626c0d-4458-45dd-8868-4ec54e21af48');
INSERT INTO public.configuracion_horario VALUES ('d14d47cf-31b0-4e80-871b-34de1197ed9c', true, 3600000000000, '439dddc4-93b6-4fa1-96f1-66a2aa350a2e');
INSERT INTO public.configuracion_horario VALUES ('ff44a7c9-8203-4e87-8e48-f4a105e299bd', true, 3600000000000, 'bb6a0e41-1ffa-45dd-857d-91e3db1cb197');
INSERT INTO public.configuracion_horario VALUES ('3c85b672-49d8-4aa6-a0ec-91c0ced5d793', true, 1800000000000, '260a2877-d18d-4b51-b752-83d2a1d8fb99');
INSERT INTO public.configuracion_horario VALUES ('ffbb1dea-9a3c-4694-8fbc-cf350d868aac', true, 3600000000000, '7a8932d3-8f05-4099-bdc0-cb48921e6deb');
INSERT INTO public.configuracion_horario VALUES ('2eae37d5-37ef-4b1a-bcfc-ab46d9e221de', true, 3600000000000, '8b0f6333-a728-4311-a05e-11717adf150b');
INSERT INTO public.configuracion_horario VALUES ('05a1a53a-c87e-48ff-ad38-d7b2628eda5c', true, 3600000000000, '6cef91de-a8ae-45e4-92ea-50b8b99f6b76');
INSERT INTO public.configuracion_horario VALUES ('4bc60f79-f0d0-4fe3-b682-20cde1b01ac3', true, 1800000000000, '0653f787-c5a0-42af-b123-3ee5c6800cf0');
INSERT INTO public.configuracion_horario VALUES ('fada7542-d25a-424f-b5b8-7b40b59c75f0', true, 3600000000000, '615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8');


--
-- Data for Name: configuracion_dia; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.configuracion_dia VALUES ('a79f74aa-e25b-4b37-979b-07d3b61c3b20', 'MONDAY', '20:00:00', '10:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('c2552d2c-9673-4720-92d1-7c1580651d99', 'TUESDAY', '16:00:00', '11:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('ab2781c6-8119-418a-8d0c-ba4a63734c03', 'WEDNESDAY', '20:00:00', '11:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('085c6f5d-cb8d-4bed-9714-bdfb5f2a397c', 'THURSDAY', '19:00:00', '08:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('7c0383a4-7fe8-4dc3-b37f-af4216bb9b70', 'FRIDAY', '16:00:00', '12:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('1b6d4fd5-5ab3-43d9-84af-68614f3ab89a', 'SATURDAY', '19:00:00', '11:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('3d9ae460-6920-4826-ae46-6992c61dfe0a', 'SUNDAY', '18:00:00', '10:00:00', 'bb1f46ee-b126-48f6-87f4-8296e8164a2e');
INSERT INTO public.configuracion_dia VALUES ('b52993f0-a203-4053-9a05-ea72d4ffb1cb', 'MONDAY', '17:00:00', '11:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('b518f856-cf20-4dbe-84c0-e1e0a39ea046', 'TUESDAY', '19:00:00', '13:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('8b5f9b66-215f-4c83-aaf2-922eabf8274b', 'WEDNESDAY', '21:00:00', '10:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('9d649530-a7bc-4103-95b0-1b95adf004fe', 'THURSDAY', '16:00:00', '11:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('66cd6e8b-b405-4dbf-8e9e-9c2b58712acb', 'FRIDAY', '20:00:00', '10:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('bef2c1ea-fadd-4707-af5f-47d0dfad5d9a', 'SATURDAY', '17:00:00', '11:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('191a96ef-294d-4c57-8c40-02b8e709dc36', 'SUNDAY', '17:00:00', '09:00:00', 'bbd1dbc3-5772-4e79-a44c-a4a8cbbe1f27');
INSERT INTO public.configuracion_dia VALUES ('55f5bc4a-0883-4984-b563-e2cee3b80780', 'MONDAY', '21:00:00', '12:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('d68df5e3-394e-4b5c-8ac3-2ca57735366f', 'TUESDAY', '21:00:00', '08:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('eea86257-80b9-4dbc-9fdc-74a3da34d01c', 'WEDNESDAY', '16:00:00', '13:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('49cf661f-a3e4-4e80-9aef-4ce94513b92b', 'THURSDAY', '16:00:00', '08:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('053c4214-61cf-459c-851a-1beae0d0776c', 'FRIDAY', '17:00:00', '10:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('0a74fbb3-267f-4586-aabf-9dd0b8bde991', 'SATURDAY', '16:00:00', '09:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('8dc5c9dc-6010-45f8-a983-b51095aedf27', 'SUNDAY', '18:00:00', '09:00:00', 'd14d47cf-31b0-4e80-871b-34de1197ed9c');
INSERT INTO public.configuracion_dia VALUES ('325e7401-58c1-4f7b-b7a5-3b6ebd29784c', 'MONDAY', '19:00:00', '11:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('3b0e3665-174e-400a-bd4c-fd83e4366c40', 'TUESDAY', '18:00:00', '09:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('86a4d54d-e2d2-4e7e-9a3c-1a511afecfb4', 'WEDNESDAY', '20:00:00', '09:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('8a464151-2bee-414f-82b6-24a4fdcc57e7', 'THURSDAY', '21:00:00', '09:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('32b38a5a-02f2-459e-b487-3b8d3ab2a97e', 'FRIDAY', '20:00:00', '12:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('8312d740-b5e8-4a3d-b96b-6a774e33c4ab', 'SATURDAY', '19:00:00', '08:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('1c2784fe-4d2e-4b55-84fb-c18df2a76cb7', 'SUNDAY', '19:00:00', '12:00:00', 'ff44a7c9-8203-4e87-8e48-f4a105e299bd');
INSERT INTO public.configuracion_dia VALUES ('ae3d24ac-6d28-4496-af8c-f224f343e1a5', 'MONDAY', '20:00:00', '12:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('a54b5089-6602-43f5-865d-a42d46a07503', 'TUESDAY', '17:00:00', '11:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('a6135890-1669-4705-8d36-5aabb5c737da', 'WEDNESDAY', '17:00:00', '12:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('edeb76a2-bb83-46a8-9abd-550991366c87', 'THURSDAY', '21:00:00', '11:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('9977e1e4-e6e6-49f5-b024-37db55d92ba7', 'FRIDAY', '19:00:00', '09:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('5c2dfe06-ba5e-44a7-ab52-75d0f3bab4e1', 'SATURDAY', '21:00:00', '08:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('83bd298a-5afc-445d-b2ba-5db792a0d91c', 'SUNDAY', '20:00:00', '12:00:00', '3c85b672-49d8-4aa6-a0ec-91c0ced5d793');
INSERT INTO public.configuracion_dia VALUES ('f7d44bfd-ebb8-48b8-b3b8-306c2a010dba', 'MONDAY', '21:00:00', '10:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('7cd0eae2-66ad-46ea-a285-2ed9b8972291', 'TUESDAY', '20:00:00', '10:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('e11ffb24-24fd-44bc-abe1-30a41d703bfb', 'WEDNESDAY', '20:00:00', '13:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('ae8a29eb-9c94-41a2-b86d-17dc0ce3475f', 'THURSDAY', '16:00:00', '12:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('74d7a55f-c987-4ac8-bd88-398fa1a6a127', 'FRIDAY', '17:00:00', '11:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('e9b8647c-8298-45ce-a600-6a534da77615', 'SATURDAY', '18:00:00', '08:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('24624a46-00d4-4daa-bfe1-58b85e944705', 'SUNDAY', '18:00:00', '10:00:00', 'ffbb1dea-9a3c-4694-8fbc-cf350d868aac');
INSERT INTO public.configuracion_dia VALUES ('03df18e0-c6eb-4e90-8a51-9ca280f0bd26', 'MONDAY', '17:00:00', '11:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('80c651a2-b72e-4b07-93dd-551a91279345', 'TUESDAY', '17:00:00', '12:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('23dc00cc-8d6f-4e0c-b98e-f671be9d2d74', 'WEDNESDAY', '21:00:00', '11:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('83051aa8-da6f-4c4c-ac3a-50ba03cd07f5', 'THURSDAY', '17:00:00', '10:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('054a1e8f-b4f7-4129-8438-3f370683fc9e', 'FRIDAY', '18:00:00', '11:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('d030b675-4152-4671-ae19-6336e9703135', 'SATURDAY', '18:00:00', '10:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('4a4e46e3-8f83-47cc-b866-ecad9f603af7', 'SUNDAY', '18:00:00', '09:00:00', '2eae37d5-37ef-4b1a-bcfc-ab46d9e221de');
INSERT INTO public.configuracion_dia VALUES ('e2d9aa0b-39d2-429c-9343-935522c00133', 'MONDAY', '16:00:00', '12:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('1aa0a88e-84af-4c18-a4f3-bfad2e6463d0', 'TUESDAY', '16:00:00', '10:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('dd972ce7-9c15-4437-8e21-274e35363cf8', 'WEDNESDAY', '20:00:00', '10:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('45575587-23d6-4458-9685-9f25fad609c5', 'THURSDAY', '19:00:00', '12:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('1df66cff-8976-45fc-a164-172e83c7d524', 'FRIDAY', '16:00:00', '08:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('008720c1-04ca-401d-b4f4-1ea215cab933', 'SATURDAY', '16:00:00', '13:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('2e1ebb3c-44c1-40ac-a9c3-b53a2ff9eacc', 'SUNDAY', '19:00:00', '12:00:00', '05a1a53a-c87e-48ff-ad38-d7b2628eda5c');
INSERT INTO public.configuracion_dia VALUES ('f67c4290-765a-41e0-9ca8-7255f6792def', 'MONDAY', '18:00:00', '13:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('929a430a-0618-4418-ba68-171ffe309b88', 'TUESDAY', '17:00:00', '13:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('a02888d4-5d05-4ba9-949e-b9a3a8188eab', 'WEDNESDAY', '17:00:00', '10:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('71bd2cb6-775c-481d-8cae-8ab486fd9f97', 'THURSDAY', '20:00:00', '12:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('5d71d824-9184-4935-91b7-7f336b680f56', 'FRIDAY', '16:00:00', '09:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('bf804e48-a140-432d-a229-24036d35703a', 'SATURDAY', '18:00:00', '11:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('67656bfe-16fb-49b9-b07f-67f9c6e2b9cf', 'SUNDAY', '18:00:00', '13:00:00', '4bc60f79-f0d0-4fe3-b682-20cde1b01ac3');
INSERT INTO public.configuracion_dia VALUES ('0070ef43-3582-4e30-b3f9-5815c59e1adb', 'MONDAY', '21:00:00', '12:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('82c79fdf-e8e9-4185-ba36-7084fd78e301', 'TUESDAY', '16:00:00', '12:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('d745943c-f1e3-4d69-9e03-42f94e236d63', 'WEDNESDAY', '17:00:00', '09:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('6dbccc0a-5d8a-401e-8de2-bcc1004d2b14', 'THURSDAY', '20:00:00', '08:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('90ee3fb4-469c-47f9-a1c7-100bf1814b1e', 'FRIDAY', '21:00:00', '12:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('4262ab69-3f7f-45c4-a54c-7e8ffaa52e0e', 'SATURDAY', '17:00:00', '08:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');
INSERT INTO public.configuracion_dia VALUES ('942e2f7f-af3e-40af-ac5f-a941fcd1b07c', 'SUNDAY', '16:00:00', '13:00:00', 'fada7542-d25a-424f-b5b8-7b40b59c75f0');


--
-- Data for Name: hello_world; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: horario_atencion; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.horario_atencion VALUES ('335a2b9a-ca2f-43db-b152-3f40ce205ada', 0, '10:00:00', '23:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('81a24bbb-a403-4677-86cf-5c7e90ab89ea', 1, '08:00:00', '23:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('ee0686b6-d19f-4f01-9d0f-e09aca3d4702', 2, '11:00:00', '23:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('1b0bc678-c70f-4526-98c6-30a9462cb67d', 3, '08:00:00', '22:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('a3d2583f-b025-4b9b-ad2d-9bcd8a3a5b7b', 4, '10:00:00', '22:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('e275b266-02e7-45b0-bcf2-2bc8152d4d59', 5, '11:00:00', '22:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('b13ab5cd-2542-4805-9965-1573664af607', 6, '11:00:00', '23:00:00', '89bdea26-82d3-4c4f-8d6d-b26d5e2f51cd');
INSERT INTO public.horario_atencion VALUES ('b3e48c15-22b2-44f1-add7-5c70999a0fd0', 0, '09:00:00', '23:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('d7990cfd-438a-4fdc-a204-f9182b4bd4b5', 1, '10:00:00', '23:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('b1154a6c-8fa2-405f-a2bc-fbc928475185', 2, '11:00:00', '22:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('c0f2e63b-4547-41a3-8c9a-8a2a7401bb78', 3, '09:00:00', '22:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('25a363f4-3797-4cf2-9c2c-aed36ae30acb', 4, '10:00:00', '23:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('37e754e1-8cf4-4d46-8669-f8c2237e4c2e', 5, '08:00:00', '23:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('c08b01ce-727a-4498-902b-75c1aa84518a', 6, '09:00:00', '22:00:00', '9fa858a1-a170-4e57-83da-ba8c448f7bc7');
INSERT INTO public.horario_atencion VALUES ('9ef8db9e-406c-475e-8f0d-43a954ae657d', 0, '08:00:00', '23:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('c613e0fb-885c-4b3b-89ed-d0aebe2b15cd', 1, '10:00:00', '22:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('355cce98-1dec-4c22-93ec-846e5c293138', 2, '11:00:00', '22:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('f0c27931-52e3-4c85-9afa-22def7db8bbe', 3, '11:00:00', '22:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('b4f7406a-725a-4874-8527-eb50849c945c', 4, '11:00:00', '23:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('a050fbd6-8602-426f-ae7f-8847465120a9', 5, '08:00:00', '22:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('53c2e50b-3d58-4021-be71-ebdf6d41bbb8', 6, '11:00:00', '23:00:00', 'ad0dd867-1120-42b4-b20b-fb297c314e74');
INSERT INTO public.horario_atencion VALUES ('e7bd7864-21b7-41f2-9244-27d158379e8e', 0, '09:00:00', '23:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('70e9c5d6-9877-4e6e-a20d-0db4729e9c53', 1, '09:00:00', '22:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('a94786cf-007b-4d53-84ce-8f29b83388e2', 2, '09:00:00', '23:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('9c23ce9d-d811-499f-9a71-2e090e3bbb60', 3, '08:00:00', '22:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('51def820-fea7-45d4-906d-ae9a35b0d1da', 4, '10:00:00', '23:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('e4b1f276-402f-47c4-8bbd-fe97e0376ea8', 5, '11:00:00', '22:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('bcd5124c-0d81-46b7-88eb-a40f1395bf2d', 6, '11:00:00', '22:00:00', 'a7d134bb-c510-49e1-9cd3-ebe1620170d5');
INSERT INTO public.horario_atencion VALUES ('114c3b7c-6df2-42f8-abc4-050fb9c11e85', 0, '09:00:00', '22:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('2b512e5a-f1ce-470f-83f9-f5ce01676ca3', 1, '11:00:00', '23:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('9879cda8-c01a-42ab-84ac-ad4b69e3b3f7', 2, '08:00:00', '22:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('fcb09c98-524f-49eb-9969-4d290d118e4c', 3, '08:00:00', '22:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('936ad9ff-fc9c-41c9-ad76-b8817f047684', 4, '08:00:00', '23:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('c1933868-eb75-4e2f-a34b-80a96dea0b9c', 5, '10:00:00', '23:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');
INSERT INTO public.horario_atencion VALUES ('42ab0d60-75a2-4968-a63a-984d122bdc87', 6, '08:00:00', '23:00:00', '988f5f45-a4ad-4b6f-8784-e84a4e28aff0');


--
-- Data for Name: reserva; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.reserva VALUES ('4c4e339d-f287-4607-a49f-e2d129fe5055', 4, 'Tenis', 'FINALIZADO', '2026-06-02', '13:30:00', '13:00:00', 'María Becerra', '19dba84a-ad98-4b70-9fca-b20475265d0c');
INSERT INTO public.reserva VALUES ('cb018637-c896-4c0f-aa6d-f76e05c2784d', 2, 'Tenis', 'PENDIENTE', '2026-06-03', '13:30:00', '13:00:00', 'Diego Silva', '19dba84a-ad98-4b70-9fca-b20475265d0c');
INSERT INTO public.reserva VALUES ('1f2ac950-85de-4da7-a5eb-7ed201f48956', 3, 'Tenis', 'CONFIRMADO', '2026-06-04', '10:30:00', '10:00:00', 'María Becerra', '19dba84a-ad98-4b70-9fca-b20475265d0c');
INSERT INTO public.reserva VALUES ('9325983e-8644-4c91-88b1-ab2975f5b826', 1, 'Tenis', 'PENDIENTE', '2026-06-05', '14:30:00', '14:00:00', 'Carlos Gómez', '19dba84a-ad98-4b70-9fca-b20475265d0c');
INSERT INTO public.reserva VALUES ('1d2252ee-46dc-49b7-b0b6-78f31cb4204e', 10, 'Fútbol', 'FINALIZADO', '2026-06-02', '15:30:00', '15:00:00', 'Lionel Messi', '53626c0d-4458-45dd-8868-4ec54e21af48');
INSERT INTO public.reserva VALUES ('552cea99-e451-4bd4-bce8-c2a0fd6b7f48', 5, 'Fútbol', 'CONFIRMADO', '2026-06-03', '12:30:00', '12:00:00', 'Martín Rodríguez', '53626c0d-4458-45dd-8868-4ec54e21af48');
INSERT INTO public.reserva VALUES ('ece5841a-9b94-44fb-af23-9420af5065a5', 8, 'Fútbol', 'CONFIRMADO', '2026-06-04', '13:30:00', '13:00:00', 'Lionel Messi', '53626c0d-4458-45dd-8868-4ec54e21af48');
INSERT INTO public.reserva VALUES ('1ad42adf-cfde-4515-8d00-d587e9ee6fee', 5, 'Fútbol', 'PENDIENTE', '2026-06-05', '12:30:00', '12:00:00', 'Martín Rodríguez', '53626c0d-4458-45dd-8868-4ec54e21af48');
INSERT INTO public.reserva VALUES ('b4435996-5b29-4889-9537-e6c74b27dd33', 10, 'Básquet', 'FINALIZADO', '2026-06-02', '11:00:00', '10:00:00', 'Lionel Messi', '439dddc4-93b6-4fa1-96f1-66a2aa350a2e');
INSERT INTO public.reserva VALUES ('c0be2dea-dcbe-4c22-962f-4fc6e00dec09', 3, 'Básquet', 'FINALIZADO', '2026-06-03', '16:00:00', '15:00:00', 'María Becerra', '439dddc4-93b6-4fa1-96f1-66a2aa350a2e');
INSERT INTO public.reserva VALUES ('e8f62d97-d042-4704-b535-36acf21a6d4c', 6, 'Básquet', 'CONFIRMADO', '2026-06-04', '11:00:00', '10:00:00', 'Diego Silva', '439dddc4-93b6-4fa1-96f1-66a2aa350a2e');
INSERT INTO public.reserva VALUES ('8c4b3b6f-f88e-480f-8f20-52fd80839da1', 5, 'Básquet', 'PENDIENTE', '2026-06-05', '13:00:00', '12:00:00', 'Martín Rodríguez', '439dddc4-93b6-4fa1-96f1-66a2aa350a2e');
INSERT INTO public.reserva VALUES ('35cdcdd3-186c-421a-8ec8-f5672a835250', 3, 'Vóley', 'FINALIZADO', '2026-06-02', '12:00:00', '11:00:00', 'Martín Rodríguez', 'bb6a0e41-1ffa-45dd-857d-91e3db1cb197');
INSERT INTO public.reserva VALUES ('e2a4b4e2-0236-431d-8678-eb342d9b0cf8', 11, 'Vóley', 'FINALIZADO', '2026-06-03', '12:00:00', '11:00:00', 'Martín Rodríguez', 'bb6a0e41-1ffa-45dd-857d-91e3db1cb197');
INSERT INTO public.reserva VALUES ('3d36e3c5-68b7-4c0d-9cfd-ec94100378a9', 1, 'Vóley', 'CONFIRMADO', '2026-06-04', '12:00:00', '11:00:00', 'Lionel Messi', 'bb6a0e41-1ffa-45dd-857d-91e3db1cb197');
INSERT INTO public.reserva VALUES ('1fb74a8c-3dec-4dd8-b402-168b3161f7c3', 2, 'Vóley', 'PENDIENTE', '2026-06-05', '15:00:00', '14:00:00', 'Diego Silva', 'bb6a0e41-1ffa-45dd-857d-91e3db1cb197');
INSERT INTO public.reserva VALUES ('42348ee2-e385-45c0-86c7-6900d58b6294', 4, 'Tenis', 'FINALIZADO', '2026-06-02', '13:30:00', '13:00:00', 'María Becerra', '260a2877-d18d-4b51-b752-83d2a1d8fb99');
INSERT INTO public.reserva VALUES ('d2c5c543-343c-40ae-9f84-5bee8d7b3c3e', 2, 'Tenis', 'FINALIZADO', '2026-06-03', '14:30:00', '14:00:00', 'Martín Rodríguez', '260a2877-d18d-4b51-b752-83d2a1d8fb99');
INSERT INTO public.reserva VALUES ('daad99bf-0266-49ec-abf2-4a630bb8d19a', 4, 'Tenis', 'FINALIZADO', '2026-06-04', '13:30:00', '13:00:00', 'Lionel Messi', '260a2877-d18d-4b51-b752-83d2a1d8fb99');
INSERT INTO public.reserva VALUES ('d3cf6f13-19ce-4470-a33b-855d01bdaa2e', 1, 'Tenis', 'PENDIENTE', '2026-06-05', '11:30:00', '11:00:00', 'Juan Pérez', '260a2877-d18d-4b51-b752-83d2a1d8fb99');
INSERT INTO public.reserva VALUES ('58a9dcc8-85dd-445a-8a22-2602e8435d20', 7, 'Básquet', 'FINALIZADO', '2026-06-02', '13:00:00', '12:00:00', 'María Becerra', '7a8932d3-8f05-4099-bdc0-cb48921e6deb');
INSERT INTO public.reserva VALUES ('1765bd93-b297-4dcd-b1e5-bd93764a11be', 6, 'Básquet', 'FINALIZADO', '2026-06-03', '16:00:00', '15:00:00', 'Juan Pérez', '7a8932d3-8f05-4099-bdc0-cb48921e6deb');
INSERT INTO public.reserva VALUES ('bbc1e63e-aba9-4958-861a-2558c8a77ff3', 5, 'Básquet', 'CONFIRMADO', '2026-06-04', '15:00:00', '14:00:00', 'Martín Rodríguez', '7a8932d3-8f05-4099-bdc0-cb48921e6deb');
INSERT INTO public.reserva VALUES ('82577917-e2c8-4345-9535-8af89f7bb8cd', 2, 'Básquet', 'CONFIRMADO', '2026-06-05', '14:00:00', '13:00:00', 'Lionel Messi', '7a8932d3-8f05-4099-bdc0-cb48921e6deb');
INSERT INTO public.reserva VALUES ('1f532738-fbfb-4d27-b1fd-5469a5c78723', 1, 'Paddle', 'FINALIZADO', '2026-06-02', '15:00:00', '14:00:00', 'Lionel Messi', '8b0f6333-a728-4311-a05e-11717adf150b');
INSERT INTO public.reserva VALUES ('70c33496-c2cc-4348-8160-aea2144a2ade', 4, 'Paddle', 'PENDIENTE', '2026-06-03', '14:00:00', '13:00:00', 'María Becerra', '8b0f6333-a728-4311-a05e-11717adf150b');
INSERT INTO public.reserva VALUES ('d832abf6-f1b2-4af3-a74d-74f19a336390', 1, 'Paddle', 'PENDIENTE', '2026-06-04', '13:00:00', '12:00:00', 'María Becerra', '8b0f6333-a728-4311-a05e-11717adf150b');
INSERT INTO public.reserva VALUES ('677af109-a687-48f4-a5ab-6eaa797f7cc2', 2, 'Paddle', 'CONFIRMADO', '2026-06-05', '14:00:00', '13:00:00', 'Juan Pérez', '8b0f6333-a728-4311-a05e-11717adf150b');
INSERT INTO public.reserva VALUES ('b7c58382-086a-4f93-b437-e21e3c73f1d5', 6, 'Fútbol', 'FINALIZADO', '2026-06-02', '13:00:00', '12:00:00', 'Carlos Gómez', '6cef91de-a8ae-45e4-92ea-50b8b99f6b76');
INSERT INTO public.reserva VALUES ('8e87e43d-b51a-4327-8bfc-9ef497ed56c8', 10, 'Fútbol', 'CONFIRMADO', '2026-06-03', '13:00:00', '12:00:00', 'Diego Silva', '6cef91de-a8ae-45e4-92ea-50b8b99f6b76');
INSERT INTO public.reserva VALUES ('8570729b-f26f-4198-ae68-debf02745d78', 3, 'Fútbol', 'PENDIENTE', '2026-06-04', '15:00:00', '14:00:00', 'Lionel Messi', '6cef91de-a8ae-45e4-92ea-50b8b99f6b76');
INSERT INTO public.reserva VALUES ('71db0825-047e-470b-a60e-2036efa9bb8a', 6, 'Fútbol', 'CONFIRMADO', '2026-06-05', '11:00:00', '10:00:00', 'María Becerra', '6cef91de-a8ae-45e4-92ea-50b8b99f6b76');
INSERT INTO public.reserva VALUES ('e7deb06d-b4cf-42c0-a75c-bf266474e05d', 2, 'Tenis', 'FINALIZADO', '2026-06-02', '15:30:00', '15:00:00', 'María Becerra', '0653f787-c5a0-42af-b123-3ee5c6800cf0');
INSERT INTO public.reserva VALUES ('79cf3ec9-051c-46a7-81b0-47db006fb3d8', 4, 'Tenis', 'FINALIZADO', '2026-06-03', '12:30:00', '12:00:00', 'Diego Silva', '0653f787-c5a0-42af-b123-3ee5c6800cf0');
INSERT INTO public.reserva VALUES ('cd8efc50-b70f-4a68-a715-551c6bd0acc7', 4, 'Tenis', 'CONFIRMADO', '2026-06-04', '14:30:00', '14:00:00', 'Diego Silva', '0653f787-c5a0-42af-b123-3ee5c6800cf0');
INSERT INTO public.reserva VALUES ('72f4c6af-7252-4657-bdfd-4b500b2026dd', 3, 'Tenis', 'PENDIENTE', '2026-06-05', '11:30:00', '11:00:00', 'Martín Rodríguez', '0653f787-c5a0-42af-b123-3ee5c6800cf0');
INSERT INTO public.reserva VALUES ('f3368db1-ed9d-4b44-be3b-17d2796000f1', 1, 'Tenis', 'FINALIZADO', '2026-06-02', '15:00:00', '14:00:00', 'Juan Pérez', '615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8');
INSERT INTO public.reserva VALUES ('bc38d53e-863c-4e43-8c32-8f297a106527', 1, 'Tenis', 'PENDIENTE', '2026-06-03', '12:00:00', '11:00:00', 'Lionel Messi', '615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8');
INSERT INTO public.reserva VALUES ('9f785d5a-1a58-4c49-8535-c1a26afa7f3a', 3, 'Tenis', 'PENDIENTE', '2026-06-04', '11:00:00', '10:00:00', 'Carlos Gómez', '615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8');
INSERT INTO public.reserva VALUES ('9b402f78-5c5a-45d5-9f2f-d289f2f6e7e9', 4, 'Tenis', 'PENDIENTE', '2026-06-05', '15:00:00', '14:00:00', 'Juan Pérez', '615bfaf0-dfa2-47e9-a1a0-4f6e125b77a8');


--
-- PostgreSQL database dump complete
--

\unrestrict HpDzzAM6O1Nw2UIkqjA5LLmBprbfsbEAd7c02DkiwD2m9xQI2uyrSmE3vDX4gFn

