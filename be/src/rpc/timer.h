// Copyright (c) 2017, Baidu.com, Inc. All Rights Reserved

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

#ifndef BDG_PALO_BE_SRC_RPC_TIMER_H
#define BDG_PALO_BE_SRC_RPC_TIMER_H

#include "fast_clock.h"
#include <cassert>
#include <chrono>
#include <cstring>

namespace palo {

/**
 * A timer class to keep timeout states across AsyncComm related calls.
 */
class Timer {
public:

    /**
     * Constructor; assigns number of milliseconds after which the timer will
     * expire
     *
     * @param millis Number of milliseconds after timer will expire
     * @param start_timer If true, timer is started immediately; otherwise
     *      start with %start
     */
    Timer(uint32_t millis, bool start_timer = false)
        : m_duration(std::chrono::milliseconds(millis)),
        m_remaining(std::chrono::milliseconds(millis)) {
            if (start_timer) {
                start();
            }
        }

    /**
     * Starts the timer. Will have no effect if the timer is still running.
     */
    void start() {
        if (!m_running) {
            start_time = std::chrono::FastClock::now();
            m_running = true;
            if (!m_started) {
                m_started = true;
            }
        }
    }

    /**
     * Stops the timer. Will assert that the timer was started. Updates the
     * remaining time (see %remaining).
     */
    void stop() {
        assert(m_started);
        auto adjustment = std::chrono::FastClock::now() - start_time;
        m_remaining = (adjustment < m_remaining) ?
            m_remaining - adjustment :
            std::chrono::FastClock::duration::zero();
        m_running = false;
    }

    /**
     * Resets the timer
     */
    void reset(bool start_timer = false) {
        m_running = false;
        m_started = false;
        m_remaining = m_duration;
        if (start_timer)
            start();
    }

    /**
     * Returns the remaining time till expiry
     * @return Remaining time in milliseconds
     */
    uint32_t remaining() {
        if (m_running) {
            stop();
            start();
        }
        return std::chrono::duration_cast<std::chrono::milliseconds>(m_remaining).count();
    }

    /**
     * Returns true if the timer is expired
     */
    bool expired() {
        return remaining() == 0;
    }

    /**
     * Returns true if the timer is still running (not yet expired
     */
    bool is_running() {
        return m_running;
    }

    /**
     * Returns the duration of the timer
     * @return Timer duration in milliseconds
     */
    uint32_t duration() {
        return (uint32_t)std::chrono::duration_cast<std::chrono::milliseconds>(m_duration).count();
    }

private:
    /** The time when the timer was started */
    std::chrono::FastClock::time_point start_time;

    /// True if the timer is running
    bool m_running {};

    /// True if the timer was started
    bool m_started {};

    /// The duration of the timer
    std::chrono::FastClock::duration m_duration;

    /// The remaining time till expiration
    std::chrono::FastClock::duration m_remaining;
};

} //namespace palo
#endif //BDG_PALO_BE_SRC_RPC_TIMER_H
