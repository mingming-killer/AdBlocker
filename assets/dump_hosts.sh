#!/bin/sh

# we need a so to make the dir
# you must set the link file as a dir you apk can access.
HOSTS_PATH="/mnt/sdcard"
#HOSTS_PATH="/data/data/com.eebbk.adblocker/app_rootkit"
HOSTS_FILE="${HOSTS_PATH}/.hosts"

echo "remount system to writable ..." 
mount -o rw,remount /dev/block/platform/emmc/by-name/system /system

echo "make a link to /etc/hosts ..." 
#echo "127.0.0.1 m.baidu.com" > ${HOSTS_PATH}/hosts
mkdir ${HOSTS_PATH}
echo "127.0.0.1 localhost" > ${HOSTS_FILE}
rm /system/etc/hosts
./busybox ln -s  ${HOSTS_FILE} /system/etc/hosts

# notice that: now you are root, so you must delete the hosts file now
# otherwise the app can't write again.
rm ${HOSTS_FILE}

echo "remount system to readonly ..." 
mount -o ro,remount /dev/block/platform/emmc/by-name/system /system

echo "all work done !" 
