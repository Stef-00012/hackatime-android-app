"use client";

import previewImage1 from "#/images/app/image1.png";
import previewImage2 from "#/images/app/image2.png";
import previewImage3 from "#/images/app/image3.png";
import previewImage4 from "#/images/app/image4.png";
import previewImage5 from "#/images/app/image5.png";
import previewImage6 from "#/images/app/image6.png";
import previewImage7 from "#/images/app/image7.png";
import previewImage8 from "#/images/app/image8.png";
import previewImage9 from "#/images/app/image9.png";
import githubButton from "#/images/github.png";
import googlePlayButton from "#/images/google-play.png";

import icon from "#/images/icon.png";
import { elevated } from "@/constants/hcColors";
import axios from "axios";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";
import Slider from "react-slick";
import { Tooltip } from "react-tooltip";

import "slick-carousel/slick/slick-theme.css";
import "slick-carousel/slick/slick.css";

const previewImages = [
    previewImage1,
    previewImage2,
    previewImage3,
    previewImage4,
    previewImage5,
    previewImage6,
    previewImage7,
    previewImage8,
    previewImage9,
]

export default function Home() {
    const [version, setVersion] = useState("Loading...")
    
    useEffect(() => {
        axios.get("https://api.github.com/repos/Stef-00012/Hackatime-Android-App/releases/latest")
            .then((res) => {
                const data = res.data

                const version = data.tag_name.split("-").shift();

                setVersion(version);
            })
    }, [])

    return (
        <>
            <header className="border-b-darkless justify-between flex items-center p-4.5 border-b border-solid">
                <div className="flex items-center">
                    <Image
                        src={icon}
                        alt="Hackatime Android Icon"
                        className="rounded-[10px]"
                        width={50}
                        height={50}
                    />
                    <p className="font-bold text-[22px] ml-3 mr-0 my-0">Hackatime Android</p>
                </div>

                <div>
                    <Link href="https://github.com/Stef-00012/Hackatime-Android-App" rel="noopener noreferrer" target="_blank">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" className="text-white transition-[color] duration-[0.3s] ease-[ease] hover:text-muted">
                            <title>GitHub</title>
                            <path fill="currentColor" d="M5.315 2.1c.791-.113 1.9.145 3.333.966l.272.161l.16.1l.397-.083a13.3 13.3 0 0 1 4.59-.08l.456.08l.396.083l.161-.1c1.385-.84 2.487-1.17 3.322-1.148l.164.008l.147.017l.076.014l.05.011l.144.047a1 1 0 0 1 .53.514a5.2 5.2 0 0 1 .397 2.91l-.047.267l-.046.196l.123.163c.574.795.93 1.728 1.03 2.707l.023.295L21 9.5c0 3.855-1.659 5.883-4.644 6.68l-.245.061l-.132.029l.014.161l.008.157l.004.365l-.002.213L16 21a1 1 0 0 1-.883.993L15 22H9a1 1 0 0 1-.993-.883L8 21v-.734c-1.818.26-3.03-.424-4.11-1.878l-.535-.766c-.28-.396-.455-.579-.589-.644l-.048-.019a1 1 0 0 1 .564-1.918c.642.188 1.074.568 1.57 1.239l.538.769c.76 1.079 1.36 1.459 2.609 1.191L8 17.562l-.018-.168a5 5 0 0 1-.021-.824l.017-.185l.019-.12l-.108-.024c-2.976-.71-4.703-2.573-4.875-6.139l-.01-.31L3 9.5a5.6 5.6 0 0 1 .908-3.051l.152-.222l.122-.163l-.045-.196a5.2 5.2 0 0 1 .145-2.642l.1-.282l.106-.253a1 1 0 0 1 .529-.514l.144-.047z"/>
                        </svg>
                    </Link>
                </div>
            </header>

            <main className="px-9 py-3 max-sm:p-3">
                <h3 className="font-bold text-xl">
                    Hackatime is an Android app to view your <Link href="https://hackatime.hackclub.com" className="text-red! hover:underline">Hackatime</Link> data.
                </h3>

                <div className="flex flex-wrap my-2">
                    <div title="Google Play is currently in closed testing">
                        <Link
                            href="https://play.google.com/store/apps/details?id=com.stefdp.hackatime"
                        >
                            <Image
                                src={googlePlayButton}
                                alt="Download on Google Play"
                            />
                        </Link>
                    </div>

                    <div>
                        <Link href="https://github.com/Stef-00012/hackatime-android-app/releases/latest" className="github-link">
                            <Image
                                src={githubButton}
                                alt="Download on GitHub"
                                className="github-logo"
                            />
                        </Link>
                    </div>
                </div>

                <Tooltip clickable anchorSelect="#som-tooltip-target" style={{
                    backgroundColor: elevated
                }}>
                    <div className="font-bold text-[1.13em] tracking-[0.01em] mb-2.5">Use the following Hackatime dummy account</div>

                    <div className="mb-2.5">
                        <span className="font-semibold text-smoke inline mb-0">API Key:</span>
                        <span className="text-red ml-1 font-mono">6d985801-1b89-43e4-9475-48f351da6657</span>

                        <span className="text-sm! text-muted block mt-2">The account linked to this API key is not linked to any slack user and will not be used for any YSWS, I just created a new mail and made an hackatime account with it</span>
                    </div>
                </Tooltip>

                <a
                    className="cursor-pointer font-semibold text-white text-[1.15em] underline! decoration-dotted"
                    id="som-tooltip-target"
                >
                    From Summer of Making?
                </a>

                <h1 className="font-bold text-5xl! my-4">Features</h1>

                <div>
                    <p className="text-xl my-2">
                        <b>View your all time data or a specific date range.</b>{" "}
                        You can view your hackatime total time, top language and top project along with a pie chart with all data for any date range.
                    </p>

                    <p className="text-xl my-2">
                        <b>View your last 7 days data.</b>{" "}
                        You can view your hackatime last 7 days time, top language, top project, top editor, top OS and top machine along with a pie chart with all data.
                    </p>

                    <p className="text-xl my-2">
                        <b>View your projects.</b>{" "}
                        You can view all your hackatime projects and also open their github repo if available.
                    </p>

                    <p className="text-xl my-2">
                        <b>Lock the app behind auth.</b>{" "}
                        You can optionally lock the app behind biometric authentication (fingerprint, face or pin code).
                    </p>
                </div>

                <h1 className="font-bold text-5xl! my-4">Preview</h1>

                <div className="flex justify-center">
                    <div className="w-75 bg-elevated p-2 pb-0 rounded-2xl">
                        <Slider
                            dots
                            swipeToSlide
                        >
                            {previewImages.map((image, index) => (
                                <div key={index}>
                                    <Image src={image} alt={`App screenshot ${index + 1}`} width={300} height={600} className="rounded-lg" />
                                </div>
                            ))}
                        </Slider>
                    </div>
                </div>

                {/* <div className="preview">
                    <div className="carousel" id="carousel">
                        <button type="button" className="carousel-arrow left" aria-label="Previous image">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                                <title>Previous</title>
                                <path fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" d="m15 6l-6 6l6 6"/>
                            </svg>
                        </button>
                        <div className="carousel-gradient carousel-gradient-left"></div>
                        <div className="carousel-track">
                            <Image src="/assets/images/app/image1.png" alt="App screenshot 1" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image2.png" alt="App screenshot 2" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image3.png" alt="App screenshot 3" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image4.png" alt="App screenshot 4" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image5.png" alt="App screenshot 5" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image6.png" alt="App screenshot 6" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image7.png" alt="App screenshot 7" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image8.png" alt="App screenshot 8" className="carousel-img" width={200} height={400} />
                            <Image src="/assets/images/app/image9.png" alt="App screenshot 9" className="carousel-img" width={200} height={400} />
                        </div>
                        <div className="carousel-gradient carousel-gradient-right"></div>
                        <button type="button" className="carousel-arrow right" aria-label="Next image">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                                <title>Next</title>
                                <path fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" d="m9 6l6 6l-6 6"/>
                            </svg>
                        </button>
                        <div className="carousel-indicators"></div>
                    </div>
                </div> */}

                {/* <script src="/assets/js/main.js"></script>
                <script src="/assets/js/tooltip.js"></script>
                <script src="/assets/js/carousel.js"></script> */}
            </main>

            <footer className="text-center p-5">
                <p className="text-xl">
                    <span>&copy; 2024 - {new Date().getFullYear()} Stef</span>
                    {" "}|{" "}
                    <span>
                        <Link href="/privacy" className="text-red!">Privacy Policy</Link>
                    </span>
                    {" "}|{" "}
                    {version}
                </p>
            </footer>
        </>
    );
}
