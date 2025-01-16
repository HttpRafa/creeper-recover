# Use the official ArchLinux image with a AUR helper installed
FROM ghcr.io/greyltc-org/archlinux-aur:paru

RUN pacman-key --init
RUN pacman -Syu --noconfirm

# Install required packages
RUN pacman -S base-devel git openssh nano jdk21-openjdk gradle --noconfirm
