---Video support.
-- 
module("vn.video", package.seeall)

-- ----------------------------------------------------------------------------
--  Variables
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--  Functions
-- ----------------------------------------------------------------------------

---Plays a full-screen video, pauses the main thread while it plays.
-- @string filename Path to a valid video file (relative to <code>res/video</code>). Supported video formats
--         are platform dependent.
function movie(filename)
    local video = Video.movie(filename)
    repeat
        yield()
    until video == nil or video:isStopped()
end
