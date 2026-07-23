import { ReactElement, useEffect, useRef, useState } from "react";
import { FaCircle } from "react-icons/fa6";

import { Icon } from "@/components/layouts/icons.tsx";

// YouTube Player type
declare global {
  interface Window {
    YT: any;
    onYouTubeIframeAPIReady: () => void;
  }
}

export default function Podcast(): ReactElement {
  const playerRef = useRef<any>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [isReady, setIsReady] = useState(false);

  // Load YouTube IFrame API
  useEffect(() => {
    if (!window.YT) {
      const tag = document.createElement("script");

      tag.src = "https://www.youtube.com/iframe_api";
      const firstScriptTag = document.getElementsByTagName("script")[0];

      firstScriptTag.parentNode?.insertBefore(tag, firstScriptTag);
    }

    window.onYouTubeIframeAPIReady = () => {
      playerRef.current = new window.YT.Player("youtube-player", {
        height: "0",
        width: "0",
        videoId: "MYPVQccHhAQ",
        playerVars: {
          autoplay: 0,
          controls: 0,
          loop: 1,
          playlist: "MYPVQccHhAQ",
        },
        events: {
          onReady: () => {
            setIsReady(true);
            setDuration(playerRef.current.getDuration());
          },
          onStateChange: (event: any) => {
            if (event.data === window.YT.PlayerState.PLAYING) {
              setIsPlaying(true);
            } else if (
              event.data === window.YT.PlayerState.PAUSED ||
              event.data === window.YT.PlayerState.ENDED
            ) {
              setIsPlaying(false);
            }
          },
        },
      });
    };

    if (window.YT && window.YT.Player) {
      window.onYouTubeIframeAPIReady();
    }
  }, []);

  // Update current time
  useEffect(() => {
    if (!isPlaying || !playerRef.current) return;

    const interval = setInterval(() => {
      if (playerRef.current && playerRef.current.getCurrentTime) {
        setCurrentTime(playerRef.current.getCurrentTime());
      }
    }, 100);

    return () => clearInterval(interval);
  }, [isPlaying]);

  // Auto-play when section comes into view
  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && playerRef.current && isReady) {
          // Try to auto-play when in view
          playerRef.current.playVideo().catch(() => {
            // Silently fail if browser blocks auto-play
          });
        } else if (!entry.isIntersecting && playerRef.current) {
          playerRef.current.pauseVideo();
        }
      },
      { threshold: 0.3 },
    );

    if (containerRef.current) {
      observer.observe(containerRef.current);
    }

    return () => observer.disconnect();
  }, [isReady]);

  const togglePlay = () => {
    if (!playerRef.current) return;

    if (isPlaying) {
      playerRef.current.pauseVideo();
    } else {
      playerRef.current.playVideo();
    }
  };

  // Try auto-play on first load when ready
  useEffect(() => {
    if (isReady && playerRef.current) {
      // Small delay to ensure player is fully ready
      setTimeout(() => {
        playerRef.current.playVideo().catch(() => {
          // Auto-play blocked - user needs to click play
        });
      }, 500);
    }
  }, [isReady]);

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const time = parseFloat(e.target.value);

    setCurrentTime(time);
    if (playerRef.current) {
      playerRef.current.seekTo(time, true);
    }
  };

  const formatTime = (time: number) => {
    const minutes = Math.floor(time / 60);
    const seconds = Math.floor(time % 60);

    return `${minutes}:${seconds.toString().padStart(2, "0")}`;
  };

  const progress = duration > 0 ? (currentTime / duration) * 100 : 0;

  return (
    <div ref={containerRef} className="px-5 md:px-[124px]">
      <div className="bg-primary p-6 md:p-8 xl:px-[40px] flex flex-col gap-6 lg:gap-8">
        {/* Podcast Label */}
        <div className="flex items-center gap-2 mb-4">
          <Icon name="airplay-alt" />
          <p className="text-white text-sm lg:text-base font-medium">
            Imaji Coffee Podcast
          </p>
        </div>

        <div className="flex gap-4 lg:gap-8 items-center">
          {/* Album Art */}
          <div className="relative w-[100px] h-[100px] md:w-[140px] md:h-[140px] lg:w-[180px] lg:h-[180px] flex-shrink-0">
            <div className="w-full h-full bg-[#000000]/40 absolute top-0 left-0 z-30" />
            <img
              alt="Coffee Jazz Podcast"
              className="object-cover absolute top-0 left-0 z-10 w-full h-full"
              src="/home/event/podcard/Image.png"
            />
            {/* Playing Animation */}
            {isPlaying && (
              <div className="absolute inset-0 flex items-center justify-center z-40">
                <div className="flex gap-1 items-end h-6 lg:h-8">
                  {[...Array(4)].map((_, i) => (
                    <div
                      key={i}
                      className="w-1 lg:w-1.5 bg-white rounded-full animate-pulse"
                      style={{
                        height: "100%",
                        animationDelay: `${i * 0.15}s`,
                        animationDuration: "1s",
                      }}
                    />
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Track Info */}
          <div className="text-white flex-1 min-w-0">
            <p className="text-base md:text-2xl lg:text-3xl font-medium mb-2 truncate">
              Mix The Taste of Indonesian Coffee
            </p>
            <p className="text-xs md:text-sm lg:text-base flex gap-2 items-center opacity-90">
              <span>Feb 2023</span> <FaCircle size={4} />{" "}
              <span>{formatTime(duration)}</span>
            </p>
          </div>

          {/* Spotify Icon */}
          <div className="hidden md:block flex-shrink-0">
            <Icon name="spotify" size={50} />
          </div>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-4">
          {/* Play/Pause Button */}
          <button
            className="w-12 h-12 lg:w-14 lg:h-14 bg-white rounded-full flex items-center justify-center hover:scale-105 transition-transform shadow-xl flex-shrink-0"
            onClick={togglePlay}
          >
            {isPlaying ? (
              <svg
                className="w-5 h-5 lg:w-6 lg:h-6 text-primary"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z" />
              </svg>
            ) : (
              <svg
                className="w-5 h-5 lg:w-6 lg:h-6 text-primary ml-0.5"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <path d="M8 5v14l11-7z" />
              </svg>
            )}
          </button>

          {/* Progress Bar */}
          <div className="flex-1">
            <input
              className="w-full h-1 lg:h-1.5 bg-white/30 appearance-none cursor-pointer [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:w-3 [&::-webkit-slider-thumb]:h-3 [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-white [&::-webkit-slider-thumb]:cursor-pointer hover:[&::-webkit-slider-thumb]:scale-110 [&::-webkit-slider-thumb]:transition-transform"
              max={duration}
              min={0}
              style={{
                background: `linear-gradient(to right, white ${progress}%, rgba(255,255,255,0.3) ${progress}%)`,
              }}
              type="range"
              value={currentTime}
              onChange={handleSeek}
            />
            <div className="flex justify-between text-xs lg:text-sm text-white/80 mt-2">
              <span>{formatTime(currentTime)}</span>
              <span>{formatTime(duration)}</span>
            </div>
          </div>
        </div>

        {/* Hidden YouTube Player */}
        <div id="youtube-player" style={{ display: "none" }} />
      </div>
    </div>
  );
}
