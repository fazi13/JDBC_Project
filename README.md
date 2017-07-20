# JDBC_Project
## first project using JDBC and SQL Server 2016

### Use the following tables to design and implement the Pomona Transit System using any database product and JDBC. 

*Those marked in **bold** are primary keys*
<p><p><p>
Trip (**TripNumber**, StartLocationName, DestinationName)

TripOffering (**TripNumber, Date, ScheduledStartTime**, SecheduledArrivalTime, DriverName, BusID)

Bus (**BusID**, Model,Year)

Driver(**DriverName**,  DriverTelephoneNumber)

Stop (**StopNumber**, StopAddress)

ActualTripStopInfo (**TripNumber, Date, ScheduledStartTime, StopNumber**, SecheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOf PassengerOut)

TripStopInfo (**TripNumber, StopNumber**, SequenceNumber, DrivingTime)
