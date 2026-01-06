=============================================================
CONCURRENT PROGRAMMING COURSEWORK - 6SENG006W
=============================================================
Student: Sahan Jayaweera
Student ID: 20220255/w2002471
Submission Date: [7th of January 2026]
Module: 6SENG006W Concurrent Programming

=============================================================
PROJECT STRUCTURE
=============================================================

ConcurrentProgrammingCW/
├── src/
│   ├── scenario1/
│   │   ├── Main.java                    - Entry point
│   │   ├── Student.java                 - Student entity (UML compliant)
│   │   ├── SubmissionStats.java         - Thread-safe statistics
│   │   └── NewSubmissionSystem.java     - Concurrent submission handler
│   │
│   └── scenario2/
│       ├── HospitalSimulation.java      - Main coordinator/bootstrap
│       ├── Patient.java                 - Patient entity
│       ├── Specialty.java               - Medical specialty enum
│       ├── PatientQueue.java            - Thread-safe queue wrapper
│       ├── PatientArrival.java          - Producer thread
│       ├── Consultant.java              - Consumer thread
│       └── ShiftManager.java            - Shift controller
│
├── README.txt                           - This file
└── TEST_RESULTS.txt                     - Complete test results

=============================================================
HOW TO RUN
=============================================================

SCENARIO 1 - University Submission System:
------------------------------------------
1. Navigate to: src/scenario1/
2. Run: Main.java
3. Select load level (1-7) from menu
4. System processes submissions concurrently
5. View comprehensive statistics

Recommended test sequence:
- Option 2 (5,000 students) - Baseline test
- Option 6 (100,000 students) - Scalability test

SCENARIO 2 - Hospital Patient Management:
------------------------------------------
1. Navigate to: src/scenario2/
2. Run: HospitalSimulation.java
3. System runs automatically through 2 shifts
4. Observe:
   - Continuous patient arrivals
   - Concurrent consultant treatment
   - Automated shift rotation
   - Queue persistence across shifts

Time Scale: 1 simulated hour = 1 real second
Total runtime: ~24 seconds (2 shifts × 12 seconds each)

=============================================================
DESIGN PATTERNS & ARCHITECTURE
=============================================================

SCENARIO 1: Thread Pool Pattern
--------------------------------
Architecture:
- Coordinator: NewSubmissionSystem
- Workers: ExecutorService thread pool (32 threads)
- Shared State: SubmissionStats (AtomicInteger counters)
- Data: Student objects

Pattern Benefits:
- Efficient thread reuse
- Bounded resource consumption
- Linear scalability demonstrated

SCENARIO 2: Producer-Consumer Pattern
--------------------------------------
Architecture:
- Producer: PatientArrival (1 thread)
- Consumers: Consultant threads (3 per shift)
- Shared Resources: PatientQueue × 3 (one per specialty)
- Controller: ShiftManager
- Bootstrap: HospitalSimulation

Pattern Benefits:
- Clear separation of concerns
- Automatic load balancing via blocking queues
- Three separate queues eliminate specialty checking
- Queue persistence across consumer lifecycle changes

Why Three Queues Instead of One:
1. Performance: No wasted CPU checking wrong specialties
2. Clarity: Each consultant has dedicated queue
3. Efficiency: Reduced thread contention
4. Correctness: Automatic specialty matching
5. Classic producer-consumer implementation

=============================================================
CONCURRENCY MECHANISMS USED
=============================================================

SCENARIO 1 - University Submission System:
-------------------------------------------

1. ExecutorService (Fixed Thread Pool)
   --------------------------------
   Purpose: Manage thousands of concurrent submission tasks

   Implementation:
   - Executors.newFixedThreadPool(32)
   - Pool size = 2 × CPU cores

   Justification:
   - Prevents thread explosion (100,000 students = 100,000 threads would crash)
   - Thread reuse: 32 threads process all tasks in batches
   - Optimal for I/O-bound operations (network simulation)

   Alternative Considered: CachedThreadPool
   - Rejected: Unbounded thread creation risk
   - With 100,000 tasks, could exhaust system resources

   Thread Safety:
   - ExecutorService handles thread lifecycle
   - Built-in synchronization for task queue

2. AtomicInteger (Lock-Free Counters)
   ----------------------------------
   Purpose: Thread-safe success/failure counting

   Implementation:
   - AtomicInteger successful
   - AtomicInteger failed

   Justification:
   - Lock-free: Uses hardware-level CAS (Compare-And-Swap)
   - High performance: No lock contention
   - Prevents race conditions in read-modify-write operations

   Alternative Considered: synchronized blocks
   - Rejected: Lock acquisition overhead reduces throughput
   - AtomicInteger is ~5-10x faster for simple increments

   Thread Safety:
   - Atomic operations guarantee no lost updates
   - Verified in testing: success + failed = exact total

3. CountDownLatch (Thread Coordination)
   ------------------------------------
   Purpose: Wait for all submission threads to complete

   Implementation:
   - CountDownLatch(numberOfStudents)
   - Each thread calls countDown() in finally block
   - Main thread blocks on await()

   Justification:
   - Purpose-built for bulk thread coordination
   - Guarantees countdown even if exceptions occur (finally block)
   - Cannot be reset: safety guarantee against premature release

   Alternative Considered: executorService.awaitTermination()
   - CountDownLatch provides more precise control
   - Explicit countdown tracking

   Thread Safety:
   - Built-in synchronization
   - Main thread safely waits for all workers

4. Student Object Design
   ---------------------
   Purpose: Encapsulate submission logic (UML requirement)

   Implementation:
   - Each Student has own Random instance
   - submitExam() returns String status

   Justification:
   - Per-student Random eliminates thread contention
   - Shared static Random would be bottleneck
   - Clean OOP design: student manages own submission

   Thread Safety:
   - Each Student object used by single thread only
   - No shared mutable state between threads

SCENARIO 2 - Hospital Patient Management:
------------------------------------------

1. LinkedBlockingQueue (Thread-Safe Buffer)
   ----------------------------------------
   Purpose: Shared buffer between producer and consumers

   Implementation:
   - Three separate LinkedBlockingQueue<Patient>
   - One queue per specialty
   - Wrapped in PatientQueue class

   Justification:
   - Built-in thread safety (no manual synchronization needed)
   - Blocking operations: put() and take()
   - Producer never blocks (unbounded queue - realistic for hospital)
   - Consumers block when empty (no busy-waiting)

   Alternative Considered: Single shared queue
   - Rejected: Requires specialty checking (wasted CPU)
   - Three queues = automatic matching, better performance

   Alternative Considered: ArrayBlockingQueue
   - Rejected: Bounded capacity could reject patients
   - Hospital cannot turn away patients (ethical requirement)

   Thread Safety:
   - Internal locks handle concurrent access
   - Safe for multiple producers and consumers
   - FIFO ordering guarantees fairness

2. Producer Thread (PatientArrival)
   ---------------------------------
   Purpose: Continuously generate patients

   Implementation:
   - Single thread running PatientArrival.run()
   - Random intervals between patients (300-700ms)
   - Random specialty assignment
   - Runs 24/7 until stopped

   Justification:
   - Models real-world: patients arrive unpredictably
   - Decoupled from consumers: arrivals continue during treatment
   - Single producer: simpler than multiple arrival points

   Thread Safety:
   - Only writes to queues (via thread-safe put())
   - No shared mutable state with consumers
   - volatile boolean for clean shutdown

3. Consumer Threads (Consultant)
   ------------------------------
   Purpose: Process patients concurrently

   Implementation:
   - Each consultant is separate thread
   - Each takes from ONE specific specialty queue
   - Blocks on take() when queue empty
   - InterruptedException signals shift end

   Justification:
   - Models real-world: consultants work independently
   - One queue per consultant: no specialty checking needed
   - Blocking behavior: efficient waiting (no CPU waste)

   Thread Safety:
   - Only reads from assigned queue (via thread-safe take())
   - No shared mutable state between consultants
   - Each maintains own patient count (thread-local)

4. Shift Management
   ----------------
   Purpose: Control consultant lifecycle without losing patients

   Implementation:
   - ShiftManager starts/stops consultant threads
   - Queues persist across shift changes
   - Thread.interrupt() signals consultants to stop
   - Thread.join() waits for graceful shutdown

   Justification:
   - Queues outlive consultant threads (correct modeling)
   - interrupt() breaks blocking take() cleanly
   - join() ensures consultants finish current patient
   - No patient data loss during transitions

   Thread Safety:
   - Each shift gets new consultant threads
   - Queues are never reset or replaced
   - No race conditions during handover

5. volatile boolean (Shutdown Flag)
   ---------------------------------
   Purpose: Signal threads to stop

   Implementation:
   - volatile boolean running in PatientArrival
   - volatile boolean working in Consultant

   Justification:
   - Ensures visibility across threads (happens-before)
   - Lightweight: no atomic operations needed for simple flag
   - Sufficient for one-way signaling

   Alternative Considered: AtomicBoolean
   - Rejected: Overkill for simple flag
   - volatile provides needed visibility guarantee

   Thread Safety:
   - volatile guarantees immediate visibility
   - No compound operations (no atomicity needed)

=============================================================
THREAD SAFETY ANALYSIS
=============================================================

SCENARIO 1 - Race Condition Prevention:
---------------------------------------
Potential Race Condition: Multiple threads incrementing counters

Solution: AtomicInteger
- Atomic incrementAndGet() operation
- Hardware-level synchronization
- No explicit locks needed

Verification:
- All tests show exact totals (success + failed = input)
- 100,000 concurrent operations: zero lost updates
- Demonstrates perfect thread safety

SCENARIO 2 - Multiple Concurrent Hazards:
-----------------------------------------
Potential Race Condition 1: Multiple threads accessing same queue

Solution: LinkedBlockingQueue
- Internal locks prevent corruption
- put() and take() are atomic operations

Verification:
- No ConcurrentModificationException observed
- No duplicate patient treatment
- All patients processed exactly once

Potential Race Condition 2: Shift transition losing patients

Solution: Queue persistence + Thread.join()
- Queues outlive consultant threads
- join() waits for current patient completion

Verification:
- Zero patients lost between shifts
- Queue sizes correctly reported
- Smooth handover demonstrated

=============================================================
LIVENESS PROPERTIES
=============================================================

SCENARIO 1:
-----------
Deadlock: IMPOSSIBLE
- No circular dependencies
- No lock ordering required
- ExecutorService manages all synchronization

Starvation: IMPOSSIBLE
- All tasks submitted to fair queue
- ExecutorService processes all tasks
- CountDownLatch guarantees completion

Progress: GUARANTEED
- System always terminates (await() returns after all tasks)
- No infinite loops or blocking

SCENARIO 2:
-----------
Deadlock: IMPOSSIBLE
- Three independent queues (no circular dependencies)
- Single resource per consultant
- No lock ordering issues

Starvation: IMPOSSIBLE
- FIFO queues guarantee fairness
- All patients eventually processed
- Blocking take() eliminates busy-waiting

Progress: GUARANTEED
- Producer always makes progress (unbounded queues)
- Consumers make progress when patients available
- InterruptedException provides clean exit

=============================================================
PERFORMANCE CHARACTERISTICS
=============================================================

SCENARIO 1 - Scalability Analysis:
----------------------------------
Throughput: ~250 submissions/second (constant across all loads)

Scalability: O(n) - Linear time complexity
- 1,000 students: 4 seconds
- 10,000 students: 40 seconds (10x)
- 100,000 students: 396 seconds (100x)

Demonstrates perfect linear scaling.

Memory: O(1) - Constant
- Thread pool size fixed at 32
- Student objects processed and garbage collected
- No memory growth with load

Resource Utilization:
- 32 threads efficiently handle 100,000 tasks
- Thread reuse prevents creation overhead
- Optimal CPU utilization for I/O-bound work

SCENARIO 2 - Throughput Analysis:
---------------------------------
Processing Rate: ~2.2 patients/second (across all consultants)

Concurrent Efficiency:
- 3 consultants working simultaneously
- Each consultant: ~0.73 patients/second
- 100ms consultation time + queue overhead

Queue Dynamics:
- Queue sizes remain small (0-5 typically)
- Arrival rate ≈ treatment rate (stable system)
- No unbounded growth observed

=============================================================
TESTING PERFORMED
=============================================================

SCENARIO 1 - Comprehensive Load Testing:
----------------------------------------
Test 1: 1,000 students - 4 seconds - 94.50% success ✓
Test 2: 5,000 students - 20 seconds - 94.58% success ✓
Test 3: 10,000 students - 40 seconds - 94.97% success ✓
Test 4: 35,000 students - 143 seconds - 95.19% success ✓
Test 5: 50,000 students - 200 seconds - 95.12% success ✓
Test 6: 100,000 students - 396 seconds - 95.00% success ✓

Thread Safety Verification:
- Every test: success + failed = exact input ✓
- No lost updates across 100,000 concurrent operations ✓
- AtomicInteger correctness verified ✓

SCENARIO 2 - Functional Testing:
--------------------------------
Producer-Consumer Pattern: VERIFIED ✓
- PatientArrival produces continuously
- Consultants consume concurrently
- Three separate queues function correctly

Shift Management: VERIFIED ✓
- Day shift → Night shift automatic rotation
- No patient loss during transition
- Consultant threads properly terminated

Specialty Matching: VERIFIED ✓
- Each consultant only treats own specialty
- Zero wrong-specialty treatments
- Three-queue design ensures correctness

Thread Safety: VERIFIED ✓
- No ConcurrentModificationException
- No race conditions observed
- Clean concurrent operations

=============================================================
COMPARISON: OLD vs NEW SYSTEMS
=============================================================

SCENARIO 1 - University Submission System:
------------------------------------------
Old System (Sequential):
- Processing: One submission at a time
- Time for 5,000: 20-30 minutes (1,200-1,800 seconds)
- Scalability: Cannot handle 100,000 students
- Issues: Timeouts, failures, student complaints

New System (Concurrent):
- Processing: 32 submissions simultaneously
- Time for 5,000: 20 seconds
- Time for 100,000: 396 seconds (6.6 minutes)
- Scalability: Proven to 100,000+
- Issues: None - graceful error handling

Performance Improvement: 60-90× FASTER

SCENARIO 2 - Hospital Patient System:
-------------------------------------
Old System (Sequential - from coursework description):
- Processing: One patient at a time
- Wait times: 4+ hours reported
- Consultants: Idle when no matching patients
- Issues: Target breaches, patient complaints

New System (Concurrent):
- Processing: 3 patients simultaneously
- Wait time: Minimal (consultation time only)
- Consultants: Active whenever patients available
- Issues: None - meets NHS targets

Performance Improvement: 3× THROUGHPUT + Eliminated idle time

=============================================================
DESIGN DECISIONS & JUSTIFICATIONS
=============================================================

SCENARIO 1:

Decision 1: Fixed Thread Pool Size = 2 × CPU Cores
---------------------------------------------------
Rationale:
- I/O-bound tasks benefit from more threads than cores
- Threads block during simulated network operations
- 2× allows context switching during I/O waits
- Higher ratios tested: diminishing returns observed

Trade-off Analysis:
- 1× cores: Underutilizes CPU during I/O waits
- 2× cores: Optimal balance (chosen)
- 4× cores: Marginal gains, higher context-switch overhead

Decision 2: AtomicInteger vs synchronized
------------------------------------------
Rationale:
- Simple counters don't need complex synchronization
- Lock-free algorithms scale better
- Hardware CAS is faster than software locks

Performance Data:
- AtomicInteger: ~5-10× faster for increments
- No lock contention = better throughput
- Verified correct in all tests

Decision 3: Separate Student Objects vs Simple IDs
---------------------------------------------------
Rationale:
- UML diagram specifies Student class
- Better OOP: encapsulation of submission logic
- Each student manages own Random (no contention)

Alternatives Rejected:
- Simple integer IDs: Simpler but violates UML spec
- Shared Random: Would become bottleneck

SCENARIO 2:

Decision 1: Three Queues vs One Shared Queue
---------------------------------------------
Rationale:
- Classic producer-consumer pattern
- Eliminates specialty checking overhead
- Reduced thread contention (three locks vs one)
- Automatic correct matching

Performance Analysis:
- One queue: Consultants waste CPU checking wrong patients
- Three queues: Direct access, zero wasted checks (chosen)

Trade-off:
- Complexity: Slightly more code
- Efficiency: Significantly better performance
- Clarity: Pattern more obvious

Decision 2: Unbounded vs Bounded Queues
----------------------------------------
Rationale:
- Hospital cannot turn away patients (ethical)
- Arrival rate ≈ treatment rate (stable in testing)
- Unbounded more realistic for emergency department

Risk Analysis:
- Bounded queue: Could reject patients (unacceptable)
- Unbounded queue: Risk of growth if arrivals >> treatments
- Mitigation: Monitor queue sizes, add consultants if needed

Decision 3: Interrupt vs Polling for Shift End
-----------------------------------------------
Rationale:
- Interrupt breaks blocking take() immediately
- Polling wastes CPU checking flag repeatedly
- Cleaner semantics: InterruptedException signals end

Implementation:
- stopWorking() sets flag
- interrupt() breaks blocking operation
- Consultant checks flag and exits cleanly

=============================================================
KNOWN LIMITATIONS & FUTURE ENHANCEMENTS
=============================================================

Current Limitations:

SCENARIO 1:
-----------
1. Fixed 5% failure rate
   - Real systems: failure rate varies with load
   - Enhancement: Dynamic failure rate based on queue depth

2. Uniform processing time (50-200ms)
   - Real systems: varied by file size, network latency
   - Enhancement: Variable times based on submission type

3. No retry mechanism
   - Enhancement: Automatic retry for failed submissions

SCENARIO 2:
-----------
1. Consultation time constant (100ms)
   - Real cases: vary by complexity
   - Enhancement: Variable consultation times by case type

2. No priority queue
   - Real hospitals: emergency cases prioritized
   - Enhancement: PriorityBlockingQueue for urgent patients

3. No breaks or shift overlap
   - Real shifts: overlap for handover
   - Enhancement: 30-minute overlap period

4. Random patient generation
   - Real hospitals: patterns (rush hours, seasonal)
   - Enhancement: Time-based arrival patterns

Future Enhancements:

1. Monitoring Dashboard
   - Real-time queue sizes
   - Consultant utilization metrics
   - Wait time tracking

2. Database Persistence
   - Patient records stored
   - Audit trail for compliance
   - Historical analysis

3. Multiple Hospitals
   - Distributed system
   - Patient transfer between locations
   - Load balancing across network

4. Realistic Time Simulation
   - Faster-than-real-time simulation
   - Configurable time scale
   - Event-driven progression

5. Advanced Scheduling
   - Consultant availability patterns
   - Planned leave management
   - Dynamic shift sizing based on load

=============================================================
LEARNING OUTCOMES ACHIEVED
=============================================================

LO1: Understand concurrent programming concepts
✓ Demonstrated through correct use of:
  - Thread pools (Scenario 1)
  - Producer-consumer pattern (Scenario 2)
  - Blocking queues
  - Atomic operations

LO2: Implement thread-safe concurrent systems
✓ Verified through:
  - Zero race conditions in testing
  - Correct synchronization mechanisms
  - Proper resource sharing

LO3: Analyze and prevent concurrency issues
✓ Shown through:
  - Deadlock prevention (no circular dependencies)
  - Starvation prevention (FIFO queues)
  - Race condition prevention (atomic operations)

LO4: Evaluate concurrent system performance
✓ Demonstrated through:
  - Scalability testing (1K to 100K students)
  - Throughput analysis
  - Performance comparisons
  - Design trade-off justifications

=============================================================
REFERENCES & RESOURCES
=============================================================

Java Documentation:
- java.util.concurrent package documentation
- ExecutorService API specification
- BlockingQueue implementations
- Atomic classes documentation

Course Materials:
- Lecture slides: Producer-Consumer pattern
- Lab exercises: Thread synchronization
- Module guidance on shift management

Academic Resources:
- "Java Concurrency in Practice" by Brian Goetz
- Oracle Java Concurrency Tutorial
- Doug Lea's concurrent programming papers

=============================================================
SUBMISSION CHECKLIST
=============================================================

Code:
✓ Scenario 1: Compiles and runs correctly
✓ Scenario 2: Compiles and runs correctly
✓ UML compliance: Student class matches diagram
✓ Thread safety: Verified through testing
✓ Comments: Code well-documented
✓ Clean code: Proper formatting and naming

Testing:
✓ Scenario 1: Tested 1K to 100K students
✓ Scenario 2: Shift rotation verified
✓ Thread safety: Race conditions prevented
✓ No crashes: Stable under all loads

Documentation:
✓ README.txt: Complete project documentation
✓ TEST_RESULTS.txt: Detailed test results
✓ Code comments: Explain concurrency choices

Vodcast:
✓ Recorded: 9-11 minute technical explanation
✓ Face visible: Throughout recording
✓ Not scripted: Natural explanation
✓ Technical depth: Level 6 appropriate
✓ Demonstrations: Both scenarios shown
✓ Justifications: Design decisions explained

Submission Files:
✓ CW.zip: Source code and documentation
✓ Vodcast.mp4: Video demonstration

=============================================================
END OF README
=============================================================