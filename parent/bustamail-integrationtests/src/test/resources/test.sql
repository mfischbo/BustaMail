set foreign_key_checks = 0;
-- MySQL dump 10.13  Distrib 5.5.37, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: bustamaildb_test
-- ------------------------------------------------------
-- Server version	5.5.37-0ubuntu0.12.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `Mailing_Mailing`
--

LOCK TABLES `Mailing_Mailing` WRITE;
/*!40000 ALTER TABLE `Mailing_Mailing` DISABLE KEYS */;
/*!40000 ALTER TABLE `Mailing_Mailing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Mailing_VersionedContent`
--

LOCK TABLES `Mailing_VersionedContent` WRITE;
/*!40000 ALTER TABLE `Mailing_VersionedContent` DISABLE KEYS */;
/*!40000 ALTER TABLE `Mailing_VersionedContent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Media_Directory`
--

LOCK TABLES `Media_Directory` WRITE;
/*!40000 ALTER TABLE `Media_Directory` DISABLE KEYS */;
INSERT INTO `Media_Directory` (`id`, `Owner_Id`, `description`, `name`, `Parent_id`) VALUES (0x159D129D9D5C459D9D5B1F9D7D2D9D33,0xAEF3B4393CA946DCA0E0CDB030F25A0C,'Root of the directory hierarchy. If you ever see this, something went terribly wrong','/',NULL),(0x3F9D5D9D534F789D209D1C194A3C9D00,0x223F9D9D473B9D9D781E1C9D615F0000,NULL,'Test Unit',NULL),(0x6D2BBEF3FF3B4ED29204E1D9B51F2C4D,0xE4EB986543A34BE79AB0D74F11A7DA9F,NULL,'Foreign Unit',NULL);
/*!40000 ALTER TABLE `Media_Directory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Media_Media`
--

LOCK TABLES `Media_Media` WRITE;
/*!40000 ALTER TABLE `Media_Media` DISABLE KEYS */;
INSERT INTO `Media_Media` (`id`, `Owner_Id`, `data`, `description`, `mimetype`, `name`, `Directory_id`) VALUES (0x3F1A399D9D459D9D9D9D9D369D6F0000,0x223F9D9D473B9D9D781E1C9D615F0000,0x7373682D727361204141414142334E7A61433179633245414141414441514142414141424151432B4366656E754D4E3343456F7933395A4B346F55554274777A62586758356865666331477A704364354E3631397A4767343037796A34315964773159584D574939617A6675786C6557733256332B4671752B6A4D314259457A7A503635433756473735515A4366482B377834424A496B2B4C30796B536C544554424A56542F7053695157704238487644433550386534617663387551584455355246456E7732476E714B6A4C336344734576424449687531796D4C724F6D71673177437648646B79764E647753356A586B2F4E31546B6E576C745A4A7153646336654474536A516E6B454A302B4441482F6A67626D326473384A617A4670322B616E566D4D4D79457A557946305441476B65782F324553585935654C2B585053434A52356E6B38476B4B2B31656B72576F377547744E46477058616471676F4C6B4D586131506B494A716A457761706D4D6E6B627450525957627420666F6F626F7840666F6F626F782D4139363041332D41390A,NULL,'text/plain','id_rsa.pub',0x3F9D5D9D534F789D209D1C194A3C9D00);
/*!40000 ALTER TABLE `Media_Media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Media_MediaImage`
--

LOCK TABLES `Media_MediaImage` WRITE;
/*!40000 ALTER TABLE `Media_MediaImage` DISABLE KEYS */;
/*!40000 ALTER TABLE `Media_MediaImage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Security_Actor`
--

LOCK TABLES `Security_Actor` WRITE;
/*!40000 ALTER TABLE `Security_Actor` DISABLE KEYS */;
INSERT INTO `Security_Actor` (`id`, `addToChildren`, `addToFutureChildren`, `OrgUnit_id`, `User_id`) VALUES (0x0F0E8CF5489A4E5F92CABB7F41431194,0x01,0x01,0x223F9D9D473B9D9D781E1C9D615F0000,0x6424309D169D42059D145D9D3E9D9D9D),(0x3485FE5920D74B86A63917DE66690476,0x01,0x01,0xE4EB986543A34BE79AB0D74F11A7DA9F,0x6424309D169D42059D145D9D3E9D9D9D),(0x3814224F68E547ED82514A0D9C138265,0x00,0x00,0xE4EB986543A34BE79AB0D74F11A7DA9F,0x36667E2241884977BD60A166BB02CB33),(0x523F26DB968C4A8E99BBC88796137BFB,0x01,0x01,0xAEF3B4393CA946DCA0E0CDB030F25A0C,0x6424309D169D42059D145D9D3E9D9D9D),(0x9D4A9D9D3F46299D229D3F4357760000,0x00,0x00,0x223F9D9D473B9D9D781E1C9D615F0000,0x6F9D0463379D469D9D28059D159D3C0F);
/*!40000 ALTER TABLE `Security_Actor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Security_Actor_Permission`
--

LOCK TABLES `Security_Actor_Permission` WRITE;
/*!40000 ALTER TABLE `Security_Actor_Permission` DISABLE KEYS */;
INSERT INTO `Security_Actor_Permission` (`Actor_id`, `Permission_id`) VALUES 
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xAA4B321DE53E415C912A21F07DCB4B17),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xE572F2553F7F4C09A87188D475725BC4),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xB9303266F7A34F5DB373BB77463AE7D4),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0x2F2FE0C63B964893B80BDA85ADFFE8FE),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0x3739BFD0E4F14B50ABB8F7FDB6E13FBF),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xF63D6ED02DEC4EA4A008C2E05AA80711),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xF754B99BA79D4F83A9CEBDBDDE2E95B9),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xF9F95C32A03146F0A9E7E3416BB8F922),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0x64E11693553B4F02998AE79CB2BC4F97),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0xBCB8F7C9E44A44EAA02B71EE3EEC7790),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0x3EA312EE637B4EE984A5446ED7CFA061),
(0x523F26DB968C4A8E99BBC88796137BFB,0x3EA312EE637B4EE984A5446ED7CFA061),
(0x523F26DB968C4A8E99BBC88796137BFB,0x2F2FE0C63B964893B80BDA85ADFFE8FE),
(0x3485FE5920D74B86A63917DE66690476,0x2F2FE0C63B964893B80BDA85ADFFE8FE),
(0x3485FE5920D74B86A63917DE66690476,0x3EA312EE637B4EE984A5446ED7CFA061),
(0x0F0E8CF5489A4E5F92CABB7F41431194,0x9F1FA3B4D7C049FE8472D52FB65AE25D);
/*!40000 ALTER TABLE `Security_Actor_Permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Security_OrgUnit`
--

LOCK TABLES `Security_OrgUnit` WRITE;
/*!40000 ALTER TABLE `Security_OrgUnit` DISABLE KEYS */;
INSERT INTO `Security_OrgUnit` (`id`, `dateCreated`, `dateModified`, `deleted`, `description`, `name`, `ParentGroup_id`) VALUES (0x223F9D9D473B9D9D781E1C9D615F0000,'2014-06-18 18:13:08','2014-06-18 18:13:08',0x00,'Unit used for testing purpose','Test Unit',0xAEF3B4393CA946DCA0E0CDB030F25A0C),(0xAEF3B4393CA946DCA0E0CDB030F25A0C,'2014-06-06 17:41:04','2014-06-06 17:41:04',0x00,'Default organizational unit','Root',NULL),(0xE4EB986543A34BE79AB0D74F11A7DA9F,'2014-06-21 16:59:09','2014-06-21 16:59:09',0x00,'Other Unit to be tested','Foreign Unit',0xAEF3B4393CA946DCA0E0CDB030F25A0C);
/*!40000 ALTER TABLE `Security_OrgUnit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Security_User`
--

LOCK TABLES `Security_User` WRITE;
/*!40000 ALTER TABLE `Security_User` DISABLE KEYS */;
INSERT INTO `Security_User` (`id`, `dateCreated`, `dateModified`, `deleted`, `email`, `firstName`, `gender`, `hidden`, `lastName`, `locked`, `password`) VALUES 
(0x36667E2241884977BD60A166BB02CB33,'2014-06-21 17:00:08','2014-06-21 17:00:08',0x00,'m.fischboeck@googlemail.com','Foreign','M',0x00,'User',0x00,'$2a$08$n9N3XBs9E7XIedSIcOVA6O0dzC87DB.uzku1WGh/..cPlM57C.1MK'),
(0x6424309D169D42059D145D9D3E9D9D9D,'2014-06-06 01:00:00','2014-06-18 17:33:34',0x00,'schdahle@art-ignition.de','Markus','M',0x00,'Fischböck',0x00,'$2a$08$n9N3XBs9E7XIedSIcOVA6O0dzC87DB.uzku1WGh/..cPlM57C.1MK'),
(0x6F9D0463379D469D9D28059D159D3C0F,'2014-06-18 18:44:32','2014-06-18 18:44:32',0x00,'testaccount@art-ignition.de','Non Permission','M',0x00,'Test-User',0x00,'$2a$08$n9N3XBs9E7XIedSIcOVA6O0dzC87DB.uzku1WGh/..cPlM57C.1MK');
/*!40000 ALTER TABLE `Security_User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Subscriber_Address`
--

LOCK TABLES `Subscriber_Address` WRITE;
/*!40000 ALTER TABLE `Subscriber_Address` DISABLE KEYS */;
/*!40000 ALTER TABLE `Subscriber_Address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Subscriber_Contact`
--

LOCK TABLES `Subscriber_Contact` WRITE;
/*!40000 ALTER TABLE `Subscriber_Contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `Subscriber_Contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Subscriber_ContactAttribute`
--

LOCK TABLES `Subscriber_ContactAttribute` WRITE;
/*!40000 ALTER TABLE `Subscriber_ContactAttribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `Subscriber_ContactAttribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SubscriptionList_List`
--

LOCK TABLES `SubscriptionList_List` WRITE;
/*!40000 ALTER TABLE `SubscriptionList_List` DISABLE KEYS */;
INSERT INTO `SubscriptionList_List` (`id`, `Owner_Id`, `description`, `name`) VALUES (0xDA66D81495BE4AD6A1F66A0EEC414ED9,0x223F9D9D473B9D9D781E1C9D615F0000,'List for testing purpose','Test Subscription List');
/*!40000 ALTER TABLE `SubscriptionList_List` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SubscriptionList_List_SubscriptionList_Subscription`
--

LOCK TABLES `SubscriptionList_List_SubscriptionList_Subscription` WRITE;
/*!40000 ALTER TABLE `SubscriptionList_List_SubscriptionList_Subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `SubscriptionList_List_SubscriptionList_Subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SubscriptionList_Subscription`
--

LOCK TABLES `SubscriptionList_Subscription` WRITE;
/*!40000 ALTER TABLE `SubscriptionList_Subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `SubscriptionList_Subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Templates_Template`
--

LOCK TABLES `Templates_Template` WRITE;
/*!40000 ALTER TABLE `Templates_Template` DISABLE KEYS */;
/*!40000 ALTER TABLE `Templates_Template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Templates_TemplateImages`
--

LOCK TABLES `Templates_TemplateImages` WRITE;
/*!40000 ALTER TABLE `Templates_TemplateImages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Templates_TemplateImages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Templates_TemplatePack`
--

LOCK TABLES `Templates_TemplatePack` WRITE;
/*!40000 ALTER TABLE `Templates_TemplatePack` DISABLE KEYS */;
/*!40000 ALTER TABLE `Templates_TemplatePack` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Templates_TemplateSettings`
--

LOCK TABLES `Templates_TemplateSettings` WRITE;
/*!40000 ALTER TABLE `Templates_TemplateSettings` DISABLE KEYS */;
/*!40000 ALTER TABLE `Templates_TemplateSettings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Templates_Widget`
--

LOCK TABLES `Templates_Widget` WRITE;
/*!40000 ALTER TABLE `Templates_Widget` DISABLE KEYS */;
/*!40000 ALTER TABLE `Templates_Widget` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-06-21 17:10:40

set foreign_key_checks=1;
