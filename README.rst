
Simple Media Scanner
********************

Show missing files
==================

What is a content provider?
---------------------------

A content provider is a database which stores information about media files
(audio files, videos and images).  Applications tell locations of files which
were created by the applications to a content provider.  Other applications can
know via the content provider where files to show are.  For example, a camera
application inserts photos' location to a content provider.  When you open the
Gallary application, that queries to the content provider about photos in your
device.  The Gallary shows the photos taken by the camera application.

What are problems?
------------------

In some cases, content providers store only partial files in your device.  When
this is realized, you cannot view all media files.

Furthermore, content providers may contain two or more duplicated information
for same one file.  In this case, you will see some same entries for the file in
viewer applications.

Show missing files
------------------

Simple Media Scanner is my solution for above problems.  This application helps
you in two ways.

1. Scanning given directories to store content providers
2. Deleting duplicated information in content providers

Permissions
===========

This application requires some permissions.

* You must allow this to write settings in your storage.
* This application must know that your tablet has been booted to run periodical
  tasks.

This is an open source software
===============================

This is open source software under the MIT license. Source code is available at
`the author's website <http://neko-daisuki.ddo.jp/~SumiTomohiko/index.html>`.

.. vim: tabstop=2 shiftwidth=2 expandtab softtabstop=2 filetype=rst